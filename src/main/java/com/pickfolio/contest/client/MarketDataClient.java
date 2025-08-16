package com.pickfolio.contest.client;

import com.pickfolio.contest.client.response.QuoteResponse;
import com.pickfolio.contest.client.response.ValidationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MarketDataClient {

    private final WebClient marketDataWebClient;

    public Mono<ValidationResponse> validateSymbol(String symbol) {
        return marketDataWebClient.get()
                .uri("/validate/{symbol}", symbol)
                .retrieve()
                .bodyToMono(ValidationResponse.class);
    }

    public Mono<QuoteResponse> getQuote(String symbol) {
        return marketDataWebClient.get()
                .uri("/quote/{symbol}", symbol)
                .retrieve()
                .bodyToMono(QuoteResponse.class);
    }
}