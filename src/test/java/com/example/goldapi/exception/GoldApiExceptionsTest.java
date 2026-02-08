package com.example.goldapi.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class GoldApiExceptionsTest {

    @Test
    void configurationExceptionStoresMessage() {
        GoldApiConfigurationException ex = new GoldApiConfigurationException("bad config");
        assertEquals("bad config", ex.getMessage());
    }

    @Test
    void unavailableExceptionStoresMessageAndCause() {
        RuntimeException cause = new RuntimeException("root");
        GoldApiUnavailableException ex = new GoldApiUnavailableException("upstream", cause);

        assertEquals("upstream", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}
