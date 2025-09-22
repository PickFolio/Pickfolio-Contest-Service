package com.pickfolio.contest.domain.response;

import java.math.BigDecimal;
import java.util.UUID;

public record LeaderboardEntryResponse(
        UUID participantId,
        UUID userId,
        BigDecimal totalPortfolioValue
) {}