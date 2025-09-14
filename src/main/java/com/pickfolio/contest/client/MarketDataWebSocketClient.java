package com.pickfolio.contest.client;

import com.pickfolio.contest.config.ExternalApiProperties;
import com.pickfolio.contest.service.LiveScoreService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
public class MarketDataWebSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(MarketDataWebSocketClient.class);
    private final LiveScoreService liveScoreService;
    private final ExternalApiProperties apiProperties;
    private final AtomicReference<WebSocketSession> sessionRef = new AtomicReference<>();

    @Scheduled(fixedDelay = 15_000)
    public void ensureConnection() {
        WebSocketSession session = sessionRef.get();
        if (session == null || !session.isOpen()) {
            logger.info("Attempting to connect to Market Data WebSocket...");
            try {
                String baseUrl = apiProperties.marketData().webSocket().baseUrl();
                String fullUrl = baseUrl + "/prices";
                StandardWebSocketClient client = new StandardWebSocketClient();
                client.execute(new PriceUpdateHandler(), fullUrl);
            } catch (Exception e) {
                logger.warn("Failed to connect to Market Data WebSocket. Will retry. Error: {}", e.getMessage());
            }
        }
    }

    private class PriceUpdateHandler extends TextWebSocketHandler {
        @Override
        public void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
            liveScoreService.updateScores(message.getPayload());
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) {
            logger.info("Connection established with Market Data Service WebSocket. Session ID: {}", session.getId());
            sessionRef.set(session);
        }

        @Override
        public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
            logger.warn("Connection closed with Market Data Service WebSocket. Status: {}. Will attempt to reconnect.", status);
            sessionRef.set(null);
        }
    }
}