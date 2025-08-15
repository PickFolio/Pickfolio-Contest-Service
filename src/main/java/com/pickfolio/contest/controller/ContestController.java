package com.pickfolio.contest.controller;

import com.pickfolio.contest.domain.request.CreateContestRequest;
import com.pickfolio.contest.domain.response.ContestResponse;
import com.pickfolio.contest.service.ContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
public class ContestController {

    private final ContestService contestService;

    @PostMapping("/create")
    public ResponseEntity<ContestResponse> createContest(@RequestBody CreateContestRequest request, @AuthenticationPrincipal Jwt jwt) {
        UUID creatorId = UUID.fromString(jwt.getSubject());
        ContestResponse createdContest = contestService.createContest(request, creatorId);
        return new ResponseEntity<>(createdContest, HttpStatus.CREATED);
    }
}