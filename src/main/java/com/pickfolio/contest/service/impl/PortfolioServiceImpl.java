package com.pickfolio.contest.service.impl;

import com.pickfolio.contest.domain.request.TransactionRequest;
import com.pickfolio.contest.domain.response.Portfolio;
import com.pickfolio.contest.service.PortfolioService;

import java.util.UUID;

public class PortfolioServiceImpl implements PortfolioService {
    @Override
    public void executeTransaction(UUID contestId, UUID userId, TransactionRequest request) {

    }

    @Override
    public Portfolio getPortfolio(UUID contestId, UUID userId) {
        return null;
    }
}
