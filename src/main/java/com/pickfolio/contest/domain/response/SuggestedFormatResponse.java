package com.pickfolio.contest.domain.response;

import java.math.BigDecimal;

public record SuggestedFormatResponse(
        String id,
        String title,
        String description,
        String suggestedName,
        BigDecimal virtualBudget,
        int durationMinutes,
        int maxParticipants
) {}
