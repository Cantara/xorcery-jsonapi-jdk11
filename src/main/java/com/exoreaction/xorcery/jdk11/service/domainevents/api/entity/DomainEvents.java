package com.exoreaction.xorcery.jdk11.service.domainevents.api.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.DELEGATING;

public final class DomainEvents {
    private final List<DomainEvent> events;


    public static DomainEvents of(DomainEvent... events) {
        return new DomainEvents(List.of(events));
    }

    @JsonCreator(mode = DELEGATING)
    public DomainEvents(List<DomainEvent> events) {
        this.events = events;
    }

    @JsonValue
    public List<DomainEvent> events() {
        return events;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DomainEvents) obj;
        return Objects.equals(this.events, that.events);
    }

    @Override
    public int hashCode() {
        return Objects.hash(events);
    }

    @Override
    public String toString() {
        return "DomainEvents[" +
                "events=" + events + ']';
    }

}
