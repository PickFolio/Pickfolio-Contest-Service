package com.pickfolio.contest.service;

import com.pickfolio.contest.domain.request.CreateContestRequest;
import com.pickfolio.contest.domain.request.JoinContestRequest;
import com.pickfolio.contest.domain.response.ContestResponse;

import java.util.List;
import java.util.UUID;

public interface ContestService {
    ContestResponse createContest(CreateContestRequest request, UUID creatorId);
    ContestResponse getContestDetails(UUID contestId);
    List<ContestResponse> findOpenPublicContests();
    void joinContest(JoinContestRequest request, UUID userId);
    void joinContestByInviteCode(JoinContestRequest request, UUID userId);
    List<String> findActiveSymbols();
    List<ContestResponse> findMyContests(UUID userId);
}
