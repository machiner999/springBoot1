package com.example.goldapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean
    public RestClient goldApiRestClient(GoldApiProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    @Bean
    public RestClient fxRestClient(GoldApiProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getFxBaseUrl())
                .build();
    }
}
