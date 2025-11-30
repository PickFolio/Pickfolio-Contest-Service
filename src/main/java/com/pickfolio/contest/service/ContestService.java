package com.pickfolio.contest.service;

import com.pickfolio.contest.client.response.QuoteResponse;
import com.pickfolio.contest.client.response.SearchResult;
import com.pickfolio.contest.domain.request.CreateContestRequest;
import com.pickfolio.contest.domain.request.JoinContestRequest;
import com.pickfolio.contest.domain.response.ContestResponse;
import com.pickfolio.contest.domain.response.LeaderboardEntryResponse;

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
    List<LeaderboardEntryResponse> getLeaderboard(UUID contestId);
    QuoteResponse getQuote(String symbol);
    List<SearchResult> searchStocks(String query);
}
