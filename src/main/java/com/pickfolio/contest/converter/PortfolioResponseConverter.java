package com.pickfolio.contest.converter;

import com.pickfolio.contest.domain.model.ContestParticipant;
import com.pickfolio.contest.domain.model.PortfolioHolding;
import com.pickfolio.contest.domain.response.Portfolio;
import com.pickfolio.contest.client.response.QuoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PortfolioResponseConverter {

    public Portfolio convert(ContestParticipant participant, List<PortfolioHolding> holdings, Map<String, QuoteResponse> quotes) {
        // Map each database holding to a response holding, enriching it with the live price
        List<com.pickfolio.contest.domain.response.PortfolioHolding> responseHoldings = holdings.stream().map(holding -> {
            BigDecimal currentPrice = quotes.get(holding.getStockSymbol()).price();
            BigDecimal currentValue = currentPrice.multiply(new BigDecimal(holding.getQuantity()));
            BigDecimal buyValue = holding.getAverageBuyPrice().multiply(new BigDecimal(holding.getQuantity()));
            return new com.pickfolio.contest.domain.response.PortfolioHolding(
                    holding.getId(),
                    holding.getStockSymbol(),
                    holding.getQuantity(),
                    holding.getAverageBuyPrice(),
                    buyValue,
                    currentValue,
                    currentValue.subtract(buyValue)
            );
        }).collect(Collectors.toList());

        // Calculate the total value of all stock holdings
        BigDecimal holdingsValue = responseHoldings.stream()
                .map(com.pickfolio.contest.domain.response.PortfolioHolding::currentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total P/L
        BigDecimal totalPL = responseHoldings.stream()
                .map(com.pickfolio.contest.domain.response.PortfolioHolding::profit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total portfolio value is cash + value of all stocks
        BigDecimal totalPortfolioValue = participant.getCashBalance().add(holdingsValue);

        return new Portfolio(
                participant.getId(),
                participant.getCashBalance(),
                holdingsValue,
                totalPortfolioValue,
                responseHoldings,
                totalPL
        );
    }
}