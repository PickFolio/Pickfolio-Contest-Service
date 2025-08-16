package com.pickfolio.contest.client.response;

import java.math.BigDecimal;

public record QuoteResponse(String symbol, BigDecimal price) {}