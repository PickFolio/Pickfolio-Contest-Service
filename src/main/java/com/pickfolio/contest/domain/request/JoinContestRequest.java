package com.pickfolio.contest.domain.request;

import java.util.UUID;

public record JoinContestRequest(
        UUID contestId,
        String inviteCode
) {}
