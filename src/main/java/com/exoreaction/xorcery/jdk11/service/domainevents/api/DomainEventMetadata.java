package com.exoreaction.xorcery.jdk11.service.domainevents.api;

import com.exoreaction.xorcery.jdk11.metadata.CommonMetadata;
import com.exoreaction.xorcery.jdk11.metadata.DeploymentMetadata;
import com.exoreaction.xorcery.jdk11.metadata.Metadata;
import com.exoreaction.xorcery.jdk11.metadata.RequestMetadata;
import com.exoreaction.xorcery.jdk11.service.domainevents.api.entity.Command;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Objects;

public final class DomainEventMetadata
        implements CommonMetadata, RequestMetadata, DeploymentMetadata {
    private final Metadata context;

    public DomainEventMetadata(Metadata context) {
        this.context = context;
    }

    public DomainEventMetadata(ObjectNode metadata) {
        this(new Metadata(metadata));
    }

    public static final class Builder
                implements CommonMetadata.Builder<Builder>,
                RequestMetadata.Builder<Builder>,
                DeploymentMetadata.Builder<Builder> {
        private final Metadata.Builder builder;

        public Builder(Metadata.Builder builder) {
            this.builder = builder;
        }

        public static DomainEventMetadata aggregateId(String aggregateId, Metadata metadata) {
                return new Builder(metadata).aggregateId(aggregateId).build();
            }

            public static DomainEventMetadata aggregateType(String aggregateType, Metadata metadata) {
                return new Builder(metadata).aggregateType(aggregateType).build();
            }

            public static DomainEventMetadata aggregate(String aggregateType, String aggregateId, Metadata metadata) {
                return new Builder(metadata)
                        .aggregateType(aggregateType)
                        .aggregateId(aggregateId)
                        .build();
            }

            public Builder(Metadata metadata) {
                this(metadata.toBuilder());
            }

            public Builder domain(String value) {
                builder.add("domain", value);
                return this;
            }

            public Builder aggregateId(String value) {
                builder.add("aggregateId", value);
                return this;
            }

            public Builder aggregateType(String name) {
                builder.add("aggregateType", name);
                return this;
            }

            public Builder commandType(Class<? extends Command> commandClass) {
                builder.add("commandType", commandClass.getName());
                return this;
            }

            public DomainEventMetadata build() {
                return new DomainEventMetadata(builder.build());
            }

        @Override
        public Metadata.Builder builder() {
            return builder;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Builder) obj;
            return Objects.equals(this.builder, that.builder);
        }

        @Override
        public int hashCode() {
            return Objects.hash(builder);
        }

        @Override
        public String toString() {
            return "Builder[" +
                    "builder=" + builder + ']';
        }

        }

    public String getDomain() {
        return context.getString("domain").orElse("default");
    }

    public String getAggregateType() {
        return context.getString("aggregateType").orElseThrow();
    }

    public String getAggregateId() {
        return context.getString("aggregateId").orElseThrow();
    }

    public String getCommandType() {
        return context.getString("commandType").orElseThrow();
    }

    @Override
    public Metadata context() {
        return context;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DomainEventMetadata) obj;
        return Objects.equals(this.context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(context);
    }

    @Override
    public String toString() {
        return "DomainEventMetadata[" +
                "context=" + context + ']';
    }


}
