package com.pickfolio.contest.domain.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record Portfolio(
        UUID participantId,
        BigDecimal cashBalance,
        BigDecimal totalPortfolioValue,
        List<PortfolioHolding> holdings
) {}