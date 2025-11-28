package com.pickfolio.contest.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickfolio.contest.domain.model.ContestParticipant;
import com.pickfolio.contest.repository.ContestParticipantRepository;
import com.pickfolio.contest.service.LiveScoreService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LiveScoreServiceImpl implements LiveScoreService {

    private static final Logger logger = LoggerFactory.getLogger(LiveScoreServiceImpl.class);
    private final ContestParticipantRepository participantRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void updateScores(String priceDataJson) {
        try {
            Map<String, Double> prices = objectMapper.readValue(priceDataJson, new TypeReference<>() {});
            if (prices.isEmpty()) {
                return;
            }
            logger.info("Received price updates for {} stocks", prices.size());

            // 1. Broadcast the raw prices to the frontend so clients can update their own portfolios instantly
            // The frontend will subscribe to "/topic/live-prices"
            messagingTemplate.convertAndSend("/topic/live-prices", prices);

            // 2. Recalculate Leaderboards (Server-side authority)
            List<ContestParticipant> activeParticipants = participantRepository.findAllWithHoldingsInLiveContests();

            for (ContestParticipant participant : activeParticipants) {
                BigDecimal holdingsValue = participant.getHoldings().stream()
                        .map(holding -> {
                            BigDecimal currentPrice = BigDecimal.valueOf(prices.getOrDefault(holding.getStockSymbol(), holding.getAverageBuyPrice().doubleValue()));
                            return currentPrice.multiply(new BigDecimal(holding.getQuantity()));
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal newTotalValue = participant.getCashBalance().add(holdingsValue);

                // Only save/broadcast if value changed noticeably (optimization to save DB writes)
                if (participant.getTotalPortfolioValue().compareTo(newTotalValue) != 0) {
                    participant.setTotalPortfolioValue(newTotalValue);
                    participantRepository.save(participant);

                    String topic = "/topic/contest/" + participant.getContest().getId();
                    messagingTemplate.convertAndSend(topic, Map.of(
                            "participantId", participant.getId(),
                            "totalPortfolioValue", newTotalValue
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to process price update", e);
        }
    }
}