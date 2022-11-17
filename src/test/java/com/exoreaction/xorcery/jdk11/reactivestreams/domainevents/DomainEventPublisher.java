package com.exoreaction.xorcery.jdk11.reactivestreams.domainevents;

import com.exoreaction.xorcery.jdk11.reactivestreams.client.api.WithMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DomainEventPublisher implements Flow.Publisher<WithMetadata<ByteBuffer>> {


    private static final AtomicInteger nextSubscriptionId = new AtomicInteger(1);
    private final List<MySubscription> subscriptions = new ArrayList<>();
    private final ObjectMapper mapper;
    private final int maxEvents;


    public DomainEventPublisher(ObjectMapper mapper, int maxEvents) {
        this.mapper = mapper;
        this.maxEvents = maxEvents;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super WithMetadata<ByteBuffer>> subscriber) {
        MySubscription subscription = new MySubscription(subscriber);
        subscriber.onSubscribe(subscription);
        subscription.start();
        subscriptions.add(subscription);
    }

    private class MySubscription implements Flow.Subscription, Runnable {

        private final int id;
        private final Flow.Subscriber<? super WithMetadata<ByteBuffer>> subscriber;
        private final AtomicLong budget = new AtomicLong(0);

        private final Thread thread;

        private volatile boolean cancelled = false;

        private final Object sync = new Object();
        final Iterator<WithMetadata<ByteBuffer>> iterator;

        private MySubscription(Flow.Subscriber<? super WithMetadata<ByteBuffer>> subscriber) {
            this.id = nextSubscriptionId.getAndIncrement();
            DomainEventGenerator eventGenerator = new DomainEventGenerator(mapper, maxEvents);
            this.subscriber = subscriber;
            iterator = eventGenerator.iterator();
            this.thread = new Thread(this);
        }

        private void start() {
            thread.start();
        }

        private void stop() {
            cancelled = true;
            try {
                thread.join();
            } catch (InterruptedException e) {
                thread.interrupt(); // reset interrupt status and ignore
            }
        }

        @Override
        public void run() {
            try {
                while (iterator.hasNext()) {
                    if (cancelled) {
                        return;
                    }
                    if (budget.get() > 0) {
                        subscriber.onNext(iterator.next());
                        budget.decrementAndGet();
                    }
                    synchronized (sync) {
                        sync.wait(100);
                    }
                }
                subscriber.onComplete();
            } catch (Throwable t) {
                throw new RuntimeException(t);
            } finally {
                subscriptions.remove(this);
            }
        }

        @Override
        public void request(long n) {
            if (n <= 0) {
                return;
            }
            budget.addAndGet(n);
            synchronized (sync) {
                sync.notify();
            }
        }

        @Override
        public void cancel() {
            cancelled = true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MySubscription that = (MySubscription) o;

            return id == that.id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }
}
