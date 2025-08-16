package com.pickfolio.contest.controller;

import com.pickfolio.contest.service.ContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/internal/contests")
@RequiredArgsConstructor
public class InternalContestController {

    private final ContestService contestService;

    @GetMapping("/active-symbols")
    public ResponseEntity<List<String>> getActiveSymbols() {
        return ResponseEntity.ok(contestService.findActiveSymbols());
    }
}