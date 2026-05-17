package com.pickfolio.contest.domain.response;

import java.util.UUID;

public record SmartAlertResponse(
        String id,
        String tone,
        String title,
        String message,
        UUID contestId,
        String type
) {}