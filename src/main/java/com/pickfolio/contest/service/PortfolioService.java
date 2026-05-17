package com.pickfolio.contest.service;

import com.pickfolio.contest.domain.request.TransactionRequest;
import com.pickfolio.contest.domain.response.Portfolio;
import com.pickfolio.contest.domain.response.SmartAlertResponse;

import java.util.List;
import java.util.UUID;

public interface PortfolioService {
    void executeTransaction(TransactionRequest request, UUID contestId, UUID userId);
    Portfolio getPortfolio(UUID contestId, UUID userId);
    List<SmartAlertResponse> getSmartAlerts(UUID userId);
}
