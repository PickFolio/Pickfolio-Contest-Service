package com.pickfolio.contest.controller;

import com.pickfolio.contest.client.response.QuoteResponse;
import com.pickfolio.contest.client.response.SearchResult;
import com.pickfolio.contest.domain.request.CreateContestRequest;
import com.pickfolio.contest.domain.request.JoinContestRequest;
import com.pickfolio.contest.domain.request.TransactionRequest;
import com.pickfolio.contest.domain.response.ContestResponse;
import com.pickfolio.contest.domain.response.LeaderboardEntryResponse;
import com.pickfolio.contest.domain.response.Portfolio;
import com.pickfolio.contest.service.ContestService;
import com.pickfolio.contest.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

import com.pickfolio.contest.domain.response.SuggestedFormatResponse;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
public class ContestController {

    private final ContestService contestService;
    private final PortfolioService portfolioService;
    private final Random random = new Random();

    private final List<SuggestedFormatResponse> suggestedFormats = Arrays.asList(
            new SuggestedFormatResponse(
                    "opening-range-15m",
                    "15-minute opening range battle",
                    "A compact contest format for fast decisions after market open. Great for testing volatility.",
                    "Morning Dash",
                    new BigDecimal("100000.00"), // 1 Lac
                    15,
                    10
            ),
            new SuggestedFormatResponse(
                    "power-hour-60m",
                    "Power Hour Showdown",
                    "A one-hour intense contest designed for the final hour of trading. High volume, high stakes.",
                    "Power Hour",
                    new BigDecimal("500000.00"), // 5 Lac
                    60,
                    20
            ),
            new SuggestedFormatResponse(
                    "penny-stock-derby",
                    "Micro-cap Derby",
                    "Test your skills with highly volatile small-cap stocks. Limited budget, maximum participants.",
                    "Penny Dash",
                    new BigDecimal("25000.00"), // 25k
                    120, // 2 hours
                    50
            ),
            new SuggestedFormatResponse(
                    "blue-chip-endurance",
                    "Blue Chip Endurance",
                    "A full-day contest focusing on stable, large-cap companies. Prove your fundamental analysis skills.",
                    "Titan's Run",
                    new BigDecimal("1000000.00"), // 10 Lac
                    375, // full market day ~6 hours 15 mins
                    5
            )
    );

    @GetMapping("/suggested-format")
    public ResponseEntity<List<SuggestedFormatResponse>> getSuggestedFormat() {
        return ResponseEntity.ok(suggestedFormats);
    }

    @PostMapping("/create")
    public ResponseEntity<ContestResponse> createContest(@RequestBody CreateContestRequest request, @AuthenticationPrincipal Jwt jwt) {
        UUID creatorId = UUID.fromString(jwt.getSubject());
        ContestResponse createdContest = contestService.createContest(request, creatorId);
        return new ResponseEntity<>(createdContest, HttpStatus.CREATED);
    }

    @GetMapping("/open-public-contests")
    public ResponseEntity<List<ContestResponse>> findOpenPublicContests() {
        List<ContestResponse> contests = contestService.findOpenPublicContests();
        return ResponseEntity.ok(contests);
    }

    @GetMapping("/details/{contestId}")
    public ResponseEntity<ContestResponse> getContestDetails(@PathVariable UUID contestId) {
        ContestResponse contest = contestService.getContestDetails(contestId);
        return ResponseEntity.ok(contest);
    }

    @PostMapping("/join")
    public ResponseEntity<Void> joinContest(@RequestBody JoinContestRequest request, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        contestService.joinContest(request, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{contestId}/transactions")
    public ResponseEntity<Void> executeTransaction(@PathVariable UUID contestId, @RequestBody TransactionRequest request, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        portfolioService.executeTransaction(request, contestId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{contestId}/portfolio")
    public ResponseEntity<Portfolio> getPortfolio(@PathVariable UUID contestId, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        Portfolio portfolio = portfolioService.getPortfolio(contestId, userId);
        return ResponseEntity.ok(portfolio);
    }

    @GetMapping("/my-contests")
    public ResponseEntity<List<ContestResponse>> findMyContests(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<ContestResponse> contests = contestService.findMyContests(userId);
        return ResponseEntity.ok(contests);
    }

    @PostMapping("/join-by-code")
    public ResponseEntity<Void> joinByCode(@RequestBody JoinContestRequest request, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        contestService.joinContestByInviteCode(request, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{contestId}/leaderboard")
    public ResponseEntity<List<LeaderboardEntryResponse>> getLeaderboard(@PathVariable UUID contestId) {
        List<LeaderboardEntryResponse> leaderboard = contestService.getLeaderboard(contestId);
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/quote/{symbol}")
    public ResponseEntity<QuoteResponse> getQuote(@PathVariable String symbol) {
        return ResponseEntity.ok(contestService.getQuote(symbol));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SearchResult>> searchStocks(@RequestParam String q) {
        return ResponseEntity.ok(contestService.searchStocks(q));
    }
}