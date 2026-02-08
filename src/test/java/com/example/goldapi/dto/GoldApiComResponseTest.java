package com.example.goldapi.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GoldApiComResponseTest {

    @Test
    void gettersAndSetters() {
        GoldApiComResponse response = new GoldApiComResponse();
        response.setSymbol("XAU");
        response.setPrice(new BigDecimal("123.45"));
        response.setUpdatedAt("2024-01-01T00:00:00Z");

        assertEquals("XAU", response.getSymbol());
        assertEquals(new BigDecimal("123.45"), response.getPrice());
        assertEquals("2024-01-01T00:00:00Z", response.getUpdatedAt());
    }
}
