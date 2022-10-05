package com.exoreaction.xorcery.configuration;

import java.util.Objects;

public final class ServiceConfiguration {
    private final Configuration configuration;

    ServiceConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration configuration() {
        return configuration;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ServiceConfiguration) obj;
        return Objects.equals(this.configuration, that.configuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configuration);
    }

    @Override
    public String toString() {
        return "ServiceConfiguration[" +
                "configuration=" + configuration + ']';
    }

    public boolean isEnabled() {
        return configuration.getBoolean("enabled").orElse(false);
    }
}
