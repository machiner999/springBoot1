package com.example.goldapi.service;

import com.example.goldapi.config.GoldApiProperties;
import com.example.goldapi.dto.GoldPriceResponse;
import com.example.goldapi.exception.GoldApiUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GoldPriceServiceTest {

    private GoldPriceService createService(RestClient goldClient, RestClient fxClient) {
        GoldApiProperties properties = new GoldApiProperties();
        return new GoldPriceService(goldClient, fxClient, properties);
    }

    @Test
    void getCurrentPrice_success() {
        RestClient.Builder goldBuilder = RestClient.builder().baseUrl("https://gold.example");
        RestClient.Builder fxBuilder = RestClient.builder().baseUrl("https://fx.example");
        MockRestServiceServer goldServer = MockRestServiceServer.bindTo(goldBuilder).build();
        MockRestServiceServer fxServer = MockRestServiceServer.bindTo(fxBuilder).build();
        RestClient goldClient = goldBuilder.build();
        RestClient fxClient = fxBuilder.build();

        goldServer.expect(requestTo("https://gold.example/price/XAU"))
                .andRespond(withSuccess("{" +
                        "\"symbol\":\"XAU\"," +
                        "\"price\":100.0," +
                        "\"updatedAt\":\"2024-01-01T00:00:00Z\"" +
                        "}", MediaType.APPLICATION_JSON));

        fxServer.expect(requestTo("https://fx.example/latest?base=USD&symbols=JPY"))
                .andRespond(withSuccess("{" +
                        "\"base\":\"USD\"," +
                        "\"rates\":{\"JPY\":150.0}" +
                        "}", MediaType.APPLICATION_JSON));

        GoldPriceService service = createService(goldClient, fxClient);
        GoldPriceResponse response = service.getCurrentPrice();

        BigDecimal expectedPerOunce = new BigDecimal("100.0")
                .multiply(new BigDecimal("150.0"))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedPerGram = new BigDecimal("100.0")
                .multiply(new BigDecimal("150.0"))
                .divide(new BigDecimal("31.1034768"), 4, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);

        assertEquals("XAU", response.metal());
        assertEquals("JPY", response.currency());
        assertEquals(expectedPerOunce, response.priceJpyPerOunce());
        assertEquals(expectedPerGram, response.priceJpyPerGram());
        assertEquals(Instant.parse("2024-01-01T00:00:00Z"), response.timestamp());

        goldServer.verify();
        fxServer.verify();
    }

    @Test
    void getCurrentPrice_goldApiServerError() {
        RestClient.Builder goldBuilder = RestClient.builder().baseUrl("https://gold.example");
        RestClient.Builder fxBuilder = RestClient.builder().baseUrl("https://fx.example");
        MockRestServiceServer goldServer = MockRestServiceServer.bindTo(goldBuilder).build();
        RestClient goldClient = goldBuilder.build();
        RestClient fxClient = fxBuilder.build();

        goldServer.expect(requestTo("https://gold.example/price/XAU"))
                .andRespond(withServerError());

        GoldPriceService service = createService(goldClient, fxClient);

        assertThrows(GoldApiUnavailableException.class, service::getCurrentPrice);
        goldServer.verify();
    }

    @Test
    void getCurrentPrice_goldApiEmptyPrice() {
        RestClient.Builder goldBuilder = RestClient.builder().baseUrl("https://gold.example");
        RestClient.Builder fxBuilder = RestClient.builder().baseUrl("https://fx.example");
        MockRestServiceServer goldServer = MockRestServiceServer.bindTo(goldBuilder).build();
        RestClient goldClient = goldBuilder.build();
        RestClient fxClient = fxBuilder.build();

        goldServer.expect(requestTo("https://gold.example/price/XAU"))
                .andRespond(withSuccess("{" +
                        "\"symbol\":\"XAU\"" +
                        "}", MediaType.APPLICATION_JSON));

        GoldPriceService service = createService(goldClient, fxClient);

        assertThrows(GoldApiUnavailableException.class, service::getCurrentPrice);
        goldServer.verify();
    }

    @Test
    void getCurrentPrice_fxApiServerError() {
        RestClient.Builder goldBuilder = RestClient.builder().baseUrl("https://gold.example");
        RestClient.Builder fxBuilder = RestClient.builder().baseUrl("https://fx.example");
        MockRestServiceServer goldServer = MockRestServiceServer.bindTo(goldBuilder).build();
        MockRestServiceServer fxServer = MockRestServiceServer.bindTo(fxBuilder).build();
        RestClient goldClient = goldBuilder.build();
        RestClient fxClient = fxBuilder.build();

        goldServer.expect(requestTo("https://gold.example/price/XAU"))
                .andRespond(withSuccess("{" +
                        "\"symbol\":\"XAU\"," +
                        "\"price\":100.0" +
                        "}", MediaType.APPLICATION_JSON));

        fxServer.expect(requestTo("https://fx.example/latest?base=USD&symbols=JPY"))
                .andRespond(withServerError());

        GoldPriceService service = createService(goldClient, fxClient);

        assertThrows(GoldApiUnavailableException.class, service::getCurrentPrice);
        goldServer.verify();
        fxServer.verify();
    }

    @Test
    void getCurrentPrice_fxMissingRate() {
        RestClient.Builder goldBuilder = RestClient.builder().baseUrl("https://gold.example");
        RestClient.Builder fxBuilder = RestClient.builder().baseUrl("https://fx.example");
        MockRestServiceServer goldServer = MockRestServiceServer.bindTo(goldBuilder).build();
        MockRestServiceServer fxServer = MockRestServiceServer.bindTo(fxBuilder).build();
        RestClient goldClient = goldBuilder.build();
        RestClient fxClient = fxBuilder.build();

        goldServer.expect(requestTo("https://gold.example/price/XAU"))
                .andRespond(withSuccess("{" +
                        "\"symbol\":\"XAU\"," +
                        "\"price\":100.0" +
                        "}", MediaType.APPLICATION_JSON));

        fxServer.expect(requestTo("https://fx.example/latest?base=USD&symbols=JPY"))
                .andRespond(withSuccess("{" +
                        "\"base\":\"USD\"," +
                        "\"rates\":{}" +
                        "}", MediaType.APPLICATION_JSON));

        GoldPriceService service = createService(goldClient, fxClient);

        assertThrows(GoldApiUnavailableException.class, service::getCurrentPrice);
        goldServer.verify();
        fxServer.verify();
    }

    @Test
    void getCurrentPrice_fxNullRates() {
        RestClient.Builder goldBuilder = RestClient.builder().baseUrl("https://gold.example");
        RestClient.Builder fxBuilder = RestClient.builder().baseUrl("https://fx.example");
        MockRestServiceServer goldServer = MockRestServiceServer.bindTo(goldBuilder).build();
        MockRestServiceServer fxServer = MockRestServiceServer.bindTo(fxBuilder).build();
        RestClient goldClient = goldBuilder.build();
        RestClient fxClient = fxBuilder.build();

        goldServer.expect(requestTo("https://gold.example/price/XAU"))
                .andRespond(withSuccess("{" +
                        "\"symbol\":\"XAU\"," +
                        "\"price\":100.0" +
                        "}", MediaType.APPLICATION_JSON));

        fxServer.expect(requestTo("https://fx.example/latest?base=USD&symbols=JPY"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        GoldPriceService service = createService(goldClient, fxClient);

        assertThrows(GoldApiUnavailableException.class, service::getCurrentPrice);
        goldServer.verify();
        fxServer.verify();
    }

    @Test
    void getCurrentPrice_blankTimestampUsesNow() {
        RestClient.Builder goldBuilder = RestClient.builder().baseUrl("https://gold.example");
        RestClient.Builder fxBuilder = RestClient.builder().baseUrl("https://fx.example");
        MockRestServiceServer goldServer = MockRestServiceServer.bindTo(goldBuilder).build();
        MockRestServiceServer fxServer = MockRestServiceServer.bindTo(fxBuilder).build();
        RestClient goldClient = goldBuilder.build();
        RestClient fxClient = fxBuilder.build();

        goldServer.expect(requestTo("https://gold.example/price/XAU"))
                .andRespond(withSuccess("{" +
                        "\"symbol\":\"XAU\"," +
                        "\"price\":100.0," +
                        "\"updatedAt\":\"\"" +
                        "}", MediaType.APPLICATION_JSON));

        fxServer.expect(requestTo("https://fx.example/latest?base=USD&symbols=JPY"))
                .andRespond(withSuccess("{" +
                        "\"base\":\"USD\"," +
                        "\"rates\":{\"JPY\":150.0}" +
                        "}", MediaType.APPLICATION_JSON));

        GoldPriceService service = createService(goldClient, fxClient);

        Instant before = Instant.now();
        GoldPriceResponse response = service.getCurrentPrice();
        Instant after = Instant.now();

        assertTrue(!response.timestamp().isBefore(before) && !response.timestamp().isAfter(after));
        goldServer.verify();
        fxServer.verify();
    }

    @Test
    void getCurrentPrice_invalidTimestampUsesNow() {
        RestClient.Builder goldBuilder = RestClient.builder().baseUrl("https://gold.example");
        RestClient.Builder fxBuilder = RestClient.builder().baseUrl("https://fx.example");
        MockRestServiceServer goldServer = MockRestServiceServer.bindTo(goldBuilder).build();
        MockRestServiceServer fxServer = MockRestServiceServer.bindTo(fxBuilder).build();
        RestClient goldClient = goldBuilder.build();
        RestClient fxClient = fxBuilder.build();

        goldServer.expect(requestTo("https://gold.example/price/XAU"))
                .andRespond(withSuccess("{" +
                        "\"symbol\":\"XAU\"," +
                        "\"price\":100.0," +
                        "\"updatedAt\":\"not-a-timestamp\"" +
                        "}", MediaType.APPLICATION_JSON));

        fxServer.expect(requestTo("https://fx.example/latest?base=USD&symbols=JPY"))
                .andRespond(withSuccess("{" +
                        "\"base\":\"USD\"," +
                        "\"rates\":{\"JPY\":150.0}" +
                        "}", MediaType.APPLICATION_JSON));

        GoldPriceService service = createService(goldClient, fxClient);

        Instant before = Instant.now();
        GoldPriceResponse response = service.getCurrentPrice();
        Instant after = Instant.now();

        assertTrue(!response.timestamp().isBefore(before) && !response.timestamp().isAfter(after));
        goldServer.verify();
        fxServer.verify();
    }
}
