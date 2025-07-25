package com.pickfolio.contest.service;

import com.pickfolio.contest.domain.request.TransactionRequest;
import com.pickfolio.contest.domain.response.Portfolio;

import java.util.UUID;

public interface PortfolioService {
    void executeTransaction(UUID contestId, UUID userId, TransactionRequest request);
    Portfolio getPortfolio(UUID contestId, UUID userId);
}
