package com.example.goldapi.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class RestClientConfigTest {

    @Test
    void buildsClientsWithBaseUrls() {
        GoldApiProperties properties = new GoldApiProperties();
        properties.setBaseUrl("https://gold.example");
        properties.setFxBaseUrl("https://fx.example");

        RestClientConfig config = new RestClientConfig();
        RestClient goldClient = config.goldApiRestClient(properties);
        RestClient fxClient = config.fxRestClient(properties);

        RestClient.Builder goldBuilder = goldClient.mutate();
        RestClient.Builder fxBuilder = fxClient.mutate();

        MockRestServiceServer goldServer = MockRestServiceServer.bindTo(goldBuilder).build();
        MockRestServiceServer fxServer = MockRestServiceServer.bindTo(fxBuilder).build();

        RestClient goldClientWithServer = goldBuilder.build();
        RestClient fxClientWithServer = fxBuilder.build();

        goldServer.expect(requestTo("https://gold.example/ping"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
        fxServer.expect(requestTo("https://fx.example/ping"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        goldClientWithServer.get().uri("/ping").retrieve().toBodilessEntity();
        fxClientWithServer.get().uri("/ping").retrieve().toBodilessEntity();

        goldServer.verify();
        fxServer.verify();
    }
}
