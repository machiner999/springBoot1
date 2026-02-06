package com.example.goldapi.service;

import com.example.goldapi.config.GoldApiProperties;
import com.example.goldapi.dto.FxRatesResponse;
import com.example.goldapi.dto.GoldApiComResponse;
import com.example.goldapi.dto.GoldPriceResponse;
import com.example.goldapi.exception.GoldApiUnavailableException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.format.DateTimeParseException;

@Service
public class GoldPriceService {
    private static final BigDecimal TROY_OUNCE_IN_GRAMS = new BigDecimal("31.1034768");
    private static final String METAL_SYMBOL = "XAU";
    private static final String TARGET_CURRENCY = "JPY";
    private static final String FX_BASE_CURRENCY = "USD";

    private final RestClient goldApiRestClient;
    private final RestClient fxRestClient;
    private final GoldApiProperties properties;

    public GoldPriceService(RestClient goldApiRestClient, RestClient fxRestClient, GoldApiProperties properties) {
        this.goldApiRestClient = goldApiRestClient;
        this.fxRestClient = fxRestClient;
        this.properties = properties;
    }

    public GoldPriceResponse getCurrentPrice() {
        GoldApiComResponse goldResponse;
        try {
            goldResponse = goldApiRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/price/" + METAL_SYMBOL)
                            .build())
                    .retrieve()
                    .body(GoldApiComResponse.class);
        } catch (RestClientException ex) {
            throw new GoldApiUnavailableException("Failed to fetch gold price from Gold API.", ex);
        }

        if (goldResponse == null || goldResponse.getPrice() == null) {
            throw new GoldApiUnavailableException("Gold API returned an empty response.", null);
        }

        FxRatesResponse fxResponse;
        try {
            fxResponse = fxRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/latest")
                            .queryParam("base", FX_BASE_CURRENCY)
                            .queryParam("symbols", TARGET_CURRENCY)
                            .build())
                    .retrieve()
                    .body(FxRatesResponse.class);
        } catch (RestClientException ex) {
            throw new GoldApiUnavailableException("Failed to fetch FX rate from upstream API.", ex);
        }

        if (fxResponse == null || fxResponse.getRates() == null
                || fxResponse.getRates().get(TARGET_CURRENCY) == null) {
            throw new GoldApiUnavailableException("FX API returned an empty response.", null);
        }

        BigDecimal usdToJpy = fxResponse.getRates().get(TARGET_CURRENCY);
        BigDecimal pricePerOunce = goldResponse.getPrice()
                .multiply(usdToJpy)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal pricePerGram = goldResponse.getPrice()
                .multiply(usdToJpy)
                .divide(TROY_OUNCE_IN_GRAMS, 4, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);

        Instant timestamp = parseTimestamp(goldResponse.getUpdatedAt());

        return new GoldPriceResponse(
                METAL_SYMBOL,
                TARGET_CURRENCY,
                pricePerOunce,
                pricePerGram,
                timestamp,
                "gold-api.com + exchangerate.host"
        );
    }

    private static Instant parseTimestamp(String raw) {
        if (raw == null || raw.isBlank()) {
            return Instant.now();
        }
        try {
            return Instant.parse(raw);
        } catch (DateTimeParseException ex) {
            return Instant.now();
        }
    }
}
