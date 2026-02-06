package com.example.goldapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "goldapi")
public class GoldApiProperties {
    private String baseUrl;
    private String apiKey;
    private String fxBaseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getFxBaseUrl() {
        return fxBaseUrl;
    }

    public void setFxBaseUrl(String fxBaseUrl) {
        this.fxBaseUrl = fxBaseUrl;
    }
}
