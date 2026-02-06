package com.example.goldapi.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record GoldPriceResponse(
        String metal,
        String currency,
        BigDecimal priceJpyPerOunce,
        BigDecimal priceJpyPerGram,
        Instant timestamp,
        String source
) {
}
