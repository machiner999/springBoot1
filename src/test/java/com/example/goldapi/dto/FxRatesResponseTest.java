package com.example.goldapi.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FxRatesResponseTest {

    @Test
    void gettersAndSetters() {
        FxRatesResponse response = new FxRatesResponse();
        response.setBase("USD");
        response.setRates(Map.of("JPY", new BigDecimal("150.00")));

        assertEquals("USD", response.getBase());
        assertEquals(new BigDecimal("150.00"), response.getRates().get("JPY"));
    }
}
