package com.pickfolio.contest.service;

import com.pickfolio.contest.domain.request.TransactionRequest;
import com.pickfolio.contest.domain.response.Portfolio;

import java.util.UUID;

public interface PortfolioService {
    void executeTransaction(TransactionRequest request, UUID contestId, UUID userId);
    Portfolio getPortfolio(UUID contestId, UUID userId);
}
