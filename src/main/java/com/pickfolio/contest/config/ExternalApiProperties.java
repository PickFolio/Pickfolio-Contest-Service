package com.pickfolio.contest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external.api.properties")
public record ExternalApiProperties(MarketData marketData) {
    public record MarketData(WebClient webClient, WebSocket webSocket) {
        public record WebClient(String baseUrl) {}
        public record WebSocket(String baseUrl) {}
    }
}