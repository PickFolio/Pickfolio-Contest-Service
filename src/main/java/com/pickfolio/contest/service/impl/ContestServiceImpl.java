package com.pickfolio.contest.service.impl;

import com.pickfolio.contest.domain.request.CreateContestRequest;
import com.pickfolio.contest.domain.request.JoinContestRequest;
import com.pickfolio.contest.domain.response.ContestResponse;
import com.pickfolio.contest.service.ContestService;

import java.util.List;
import java.util.UUID;

public class ContestServiceImpl implements ContestService {
    @Override
    public ContestResponse createContest(CreateContestRequest request, UUID creatorId) {
        return null;
    }

    @Override
    public ContestResponse getContestDetails(UUID contestId) {
        return null;
    }

    @Override
    public List<ContestResponse> findOpenPublicContests() {
        return List.of();
    }

    @Override
    public void joinContest(JoinContestRequest request, UUID userId) {

    }
}
