package com.example.goldapi.controller;

import com.example.goldapi.dto.GoldPriceResponse;
import com.example.goldapi.service.GoldPriceService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GoldPriceControllerTest {

    @Test
    void getPrice_delegatesToService() {
        GoldPriceService service = mock(GoldPriceService.class);
        GoldPriceResponse expected = new GoldPriceResponse(
                "XAU",
                "JPY",
                new BigDecimal("1000.00"),
                new BigDecimal("32.15"),
                Instant.parse("2024-01-01T00:00:00Z"),
                "source"
        );
        when(service.getCurrentPrice()).thenReturn(expected);

        GoldPriceController controller = new GoldPriceController(service);
        GoldPriceResponse actual = controller.getPrice();

        assertEquals(expected, actual);
        verify(service).getCurrentPrice();
    }
}
