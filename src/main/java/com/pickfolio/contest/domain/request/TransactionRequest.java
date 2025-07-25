package com.pickfolio.contest.domain.request;

import com.pickfolio.contest.constant.TransactionType;

public record TransactionRequest(
        String stockSymbol,
        TransactionType transactionType,
        int quantity
) {}