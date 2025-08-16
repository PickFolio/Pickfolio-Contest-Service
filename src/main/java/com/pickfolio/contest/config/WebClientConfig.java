package com.pickfolio.contest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${external.api.properties.market-data.base-url}")
    private String marketDataBaseUrl;

    @Bean
    public WebClient marketDataWebClient() {
        return WebClient.builder()
                .baseUrl(marketDataBaseUrl)
                .build();
    }
}