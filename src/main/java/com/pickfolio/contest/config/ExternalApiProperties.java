package com.pickfolio.contest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external.api.properties")
public record ExternalApiProperties(MarketData marketData, AuthService authService) {

    public record MarketData(WebClient webClient, WebSocket webSocket) {
        public record WebClient(String baseUrl) {}
        public record WebSocket(String baseUrl) {}
    }

    public record AuthService(WebClient webClient) {
        public record WebClient(String baseUrl) {}
    }
}