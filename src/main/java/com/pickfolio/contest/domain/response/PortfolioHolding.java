package com.pickfolio.contest.domain.response;

import java.math.BigDecimal;
import java.util.UUID;

public record PortfolioHolding(
        UUID id,
        String stockSymbol,
        int quantity,
        BigDecimal averageBuyPrice,
        BigDecimal buyValue,
        BigDecimal currentValue, // This will be enriched by the Market Data service
        BigDecimal profit
) {}