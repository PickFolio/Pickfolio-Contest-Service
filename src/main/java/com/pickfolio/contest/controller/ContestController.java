package com.pickfolio.contest.controller;

import com.pickfolio.contest.domain.request.CreateContestRequest;
import com.pickfolio.contest.domain.request.JoinContestRequest;
import com.pickfolio.contest.domain.request.TransactionRequest;
import com.pickfolio.contest.domain.response.ContestResponse;
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

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
public class ContestController {

    private final ContestService contestService;
    private final PortfolioService portfolioService;

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
}