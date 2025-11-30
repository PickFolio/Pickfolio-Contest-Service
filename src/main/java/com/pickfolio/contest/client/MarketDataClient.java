package com.pickfolio.contest.client;

import com.pickfolio.contest.client.response.QuoteResponse;
import com.pickfolio.contest.client.response.SearchResult; // Import
import com.pickfolio.contest.client.response.ValidationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux; // Import Flux
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

    public Flux<SearchResult> searchSymbols(String query) {
        return marketDataWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .bodyToFlux(SearchResult.class);
    }
}