package com.exoreaction.xorcery.configuration;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigurationTest {

    @Test
    void thatNavigationWorks() {
        String value = new Configuration.Builder()
                .add("foo.bar.baz", "precious")
                .build()
                .getString("foo.bar.baz")
                .orElseThrow();
        assertEquals("precious", value);
    }
}