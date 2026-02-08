package com.example.goldapi.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GoldPriceResponseTest {

    @Test
    void recordAccessors() {
        GoldPriceResponse response = new GoldPriceResponse(
                "XAU",
                "JPY",
                new BigDecimal("1000.00"),
                new BigDecimal("32.15"),
                Instant.parse("2024-01-01T00:00:00Z"),
                "source"
        );

        assertEquals("XAU", response.metal());
        assertEquals("JPY", response.currency());
        assertEquals(new BigDecimal("1000.00"), response.priceJpyPerOunce());
        assertEquals(new BigDecimal("32.15"), response.priceJpyPerGram());
        assertEquals(Instant.parse("2024-01-01T00:00:00Z"), response.timestamp());
        assertEquals("source", response.source());
    }
}
