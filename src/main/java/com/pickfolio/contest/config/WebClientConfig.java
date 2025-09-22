package com.pickfolio.contest.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final ExternalApiProperties externalApiProperties;

    @Bean
    public WebClient marketDataWebClient() {
        return WebClient.builder()
                .baseUrl(externalApiProperties.marketData().webClient().baseUrl())
                .build();
    }

    @Bean
    public WebClient authServiceWebClient() {
        return WebClient.builder()
                .baseUrl(externalApiProperties.authService().webClient().baseUrl())
                .build();
    }
}