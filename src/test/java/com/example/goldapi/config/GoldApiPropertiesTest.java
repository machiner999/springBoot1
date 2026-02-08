package com.example.goldapi.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GoldApiPropertiesTest {

    @Test
    void gettersAndSetters() {
        GoldApiProperties properties = new GoldApiProperties();
        properties.setBaseUrl("https://gold.example");
        properties.setApiKey("secret");
        properties.setFxBaseUrl("https://fx.example");

        assertEquals("https://gold.example", properties.getBaseUrl());
        assertEquals("secret", properties.getApiKey());
        assertEquals("https://fx.example", properties.getFxBaseUrl());
    }
}
