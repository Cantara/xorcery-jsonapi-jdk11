package com.exoreaction.xorcery.jdk11.configuration;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

/**
 * @author rickardoberg
 * @since 20/04/2022
 */

public interface StandardConfiguration {
    static final class Impl
                implements StandardConfiguration {
        private final Configuration configuration;

        public Impl(Configuration configuration) {
            this.configuration = configuration;
        }

        @Override
        public Configuration configuration() {
            return configuration;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Impl) obj;
            return Objects.equals(this.configuration, that.configuration);
        }

        @Override
        public int hashCode() {
            return Objects.hash(configuration);
        }

        @Override
        public String toString() {
            return "Impl[" +
                    "configuration=" + configuration + ']';
        }

        }

    Configuration configuration();

    default String getId() {
        return configuration().getString("id").orElse(null);
    }

    default String getHost() {
        return configuration().getString("host").orElse(null);
    }

    default String getEnvironment() {
        return configuration().getString("environment").orElse(null);
    }

    default String getTag() {
        return configuration().getString("tag").orElse(null);
    }

    default String getHome() {
        return configuration().getString("home").orElseGet(() ->
        {
            try {
                return new File(".").getCanonicalPath();
            } catch (IOException e) {
                return new File(".").getAbsolutePath();
            }
        });
    }

    default URI getServerUri() {
        return configuration().getURI("server.uri").orElseThrow();
    }

    /*
    default jakarta.ws.rs.core.UriBuilder getServerUriBuilder() {
        return jakarta.ws.rs.core.UriBuilder.fromUri(getServerUri());
    }
     */
}
