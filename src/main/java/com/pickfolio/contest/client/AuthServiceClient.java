package com.pickfolio.contest.client;

import com.pickfolio.contest.client.response.UserDetailResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
public class AuthServiceClient {

    private final WebClient authServiceWebClient;

    public AuthServiceClient(@Qualifier("authServiceWebClient") WebClient authServiceWebClient) {
        this.authServiceWebClient = authServiceWebClient;
    }

    public Mono<List<UserDetailResponse>> getUsersDetails(List<UUID> userIds) {
        return authServiceWebClient.post()
                .uri("/api/internal/users/details")
                .bodyValue(userIds)
                .retrieve()
                .bodyToFlux(UserDetailResponse.class)
                .collectList();
    }
}
