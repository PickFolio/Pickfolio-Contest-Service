package com.pickfolio.contest.domain.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateContestRequest(
        String name,
        boolean isPrivate,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BigDecimal virtualBudget,
        int maxParticipants
) {}