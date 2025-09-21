package com.pickfolio.contest.domain.response;

import com.pickfolio.contest.constant.ContestStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ContestResponse(
        UUID id,
        String name,
        ContestStatus status,
        boolean isPrivate,
        String inviteCode,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BigDecimal virtualBudget,
        int maxParticipants,
        int currentParticipants,
        UUID creatorId,
        LocalDateTime createdAt
) {}