package com.exoreaction.xorcery.jdk11.service.domainevents.api;

import com.exoreaction.xorcery.jdk11.metadata.Metadata;
import com.exoreaction.xorcery.jdk11.service.domainevents.api.entity.DomainEvents;

public interface DomainEventPublisher {
    void publish(Metadata metadata, DomainEvents events);
}
