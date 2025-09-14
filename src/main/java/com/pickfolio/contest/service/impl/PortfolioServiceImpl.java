package com.pickfolio.contest.service.impl;

import com.pickfolio.contest.client.MarketDataClient;
import com.pickfolio.contest.client.response.QuoteResponse;
import com.pickfolio.contest.client.response.ValidationResponse;
import com.pickfolio.contest.constant.ContestStatus;
import com.pickfolio.contest.constant.TransactionType;
import com.pickfolio.contest.converter.PortfolioResponseConverter;
import com.pickfolio.contest.domain.model.ContestParticipant;
import com.pickfolio.contest.domain.model.PortfolioHolding;
import com.pickfolio.contest.domain.model.Transaction;
import com.pickfolio.contest.domain.request.TransactionRequest;
import com.pickfolio.contest.domain.response.Portfolio;
import com.pickfolio.contest.exception.*;
import com.pickfolio.contest.repository.ContestParticipantRepository;
import com.pickfolio.contest.repository.PortfolioHoldingRepository;
import com.pickfolio.contest.repository.TransactionRepository;
import com.pickfolio.contest.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioServiceImpl implements PortfolioService {
    private final ContestParticipantRepository participantRepository;
    private final PortfolioHoldingRepository holdingRepository;
    private final TransactionRepository transactionRepository;
    private final MarketDataClient marketDataClient;
    private final PortfolioResponseConverter portfolioResponseConverter;

    @Override
    @Transactional
    public void executeTransaction(TransactionRequest request, UUID contestId, UUID userId) {

        if (request == null || request.transactionType() == null || !(request.transactionType() == TransactionType.BUY || request.transactionType() == TransactionType.SELL) || request.stockSymbol() == null || request.quantity() <= 0) {
            log.error("Invalid transaction request: {}", request);
            throw new InvalidTransactionRequestException("Invalid transaction request");
        }

        log.debug("Executing transaction: type={}, symbol={}, quantity={}, contestId={}, userId={}",
                request.transactionType(), request.stockSymbol(), request.quantity(), contestId, userId);

        ContestParticipant participant = participantRepository.findByContestIdAndUserId(contestId, userId)
                .orElseThrow(() -> {
                    log.warn("Participant not found for contestId={}, userId={}", contestId, userId);
                    return new ParticipantNotFoundException("Participant not found");
                });

        if(participant.getContest().getStatus() == ContestStatus.COMPLETED || participant.getContest().getStatus() == ContestStatus.CANCELLED) {
            log.warn("Contest is not open for transactions: contestId={}, status={}", contestId, participant.getContest().getStatus());
            throw new ContestNotOpenException("Contest is not open for transactions");
        }

        ValidationResponse validation = marketDataClient.validateSymbol(request.stockSymbol()).block();
        if (validation == null || !validation.isValid()) {
            log.warn("Invalid stock symbol: {}", request.stockSymbol());
            throw new InvalidStockSymbolException("Stock symbol is not valid: " + request.stockSymbol());
        }

        QuoteResponse quote = marketDataClient.getQuote(request.stockSymbol()).block();
        if (quote == null) {
            log.warn("Could not retrieve price for symbol: {}", request.stockSymbol());
            throw new InvalidStockSymbolException("Could not retrieve price for symbol: " + request.stockSymbol());
        }

        BigDecimal currentPrice = quote.price();
        log.debug("Current price for {}: {}", request.stockSymbol(), currentPrice);

        if (request.transactionType() == TransactionType.BUY) {
            log.debug("Processing BUY transaction for userId={}, symbol={}, quantity={} for price per share={}", userId, request.stockSymbol(), request.quantity(), currentPrice);
            executeBuy(participant, request, currentPrice);
        } else {
            log.debug("Processing SELL transaction for userId={}, symbol={}, quantity={} for price per share={}", userId, request.stockSymbol(), request.quantity(), currentPrice);
            executeSell(participant, request, currentPrice);
        }
    }

    private void executeBuy(ContestParticipant participant, TransactionRequest request, BigDecimal price) {

        BigDecimal totalCost = price.multiply(new BigDecimal(request.quantity()));
        log.debug("Total cost for BUY: {}", totalCost);

        if (participant.getCashBalance().compareTo(totalCost) < 0) {
            log.warn("Insufficient funds: participantId={}, cashBalance={}, totalCost={}",
                    participant.getId(), participant.getCashBalance(), totalCost);
            throw new InsufficientFundsException("Insufficient funds to complete purchase.");
        }

        PortfolioHolding holding = holdingRepository
                .findByParticipantIdAndStockSymbol(participant.getId(), request.stockSymbol())
                .orElseGet(() -> {
                    log.debug("No existing holding found, creating new holding for participantId={}, symbol={}",
                            participant.getId(), request.stockSymbol());
                    return PortfolioHolding.builder()
                            .participant(participant)
                            .stockSymbol(request.stockSymbol())
                            .quantity(0)
                            .averageBuyPrice(BigDecimal.ZERO)
                            .build();
                });

        BigDecimal existingValue = holding.getAverageBuyPrice().multiply(new BigDecimal(holding.getQuantity()));
        BigDecimal newValue = existingValue.add(totalCost);
        int newQuantity = holding.getQuantity() + request.quantity();
        BigDecimal newAveragePrice = newValue.divide(new BigDecimal(newQuantity), 4, RoundingMode.HALF_UP);

        log.debug("Updating holding: oldQuantity={}, newQuantity={}, oldAvgPrice={}, newAvgPrice={}",
                holding.getQuantity(), newQuantity, holding.getAverageBuyPrice(), newAveragePrice);

        holding.setQuantity(newQuantity);
        holding.setAverageBuyPrice(newAveragePrice);
        holdingRepository.save(holding);

        participant.getHoldings().add(holding);
        participant.setCashBalance(participant.getCashBalance().subtract(totalCost));
        participantRepository.save(participant);
        log.info("Updated participant cash balance: participantId={}, newBalance={}", participant.getId(), participant.getCashBalance());

        Transaction transaction = Transaction.builder()
                .holding(holding)
                .transactionType(TransactionType.BUY)
                .quantity(request.quantity())
                .pricePerShare(price)
                .build();

        transactionRepository.save(transaction);
        log.info("Completed BUY transaction: participantId={}, symbol={}, quantity={}, price={}",
                participant.getId(), request.stockSymbol(), request.quantity(), price);
    }

    private void executeSell(ContestParticipant participant, TransactionRequest request, BigDecimal price) {
        PortfolioHolding holding = holdingRepository
                .findByParticipantIdAndStockSymbol(participant.getId(), request.stockSymbol())
                .orElseThrow(() -> {
                    log.warn("No holding found for participantId={}, symbol={}", participant.getId(), request.stockSymbol());
                    return new InsufficientHoldingsException("Player does not hold the specified stock symbol.");
                });

        if (holding.getQuantity() < request.quantity()) {
            log.warn("Insufficient holdings: participantId={}, symbol={}, requestedQuantity={}, availableQuantity={}",
                    participant.getId(), request.stockSymbol(), request.quantity(), holding.getQuantity());
            throw new InsufficientHoldingsException("Insufficient holdings to complete sell transaction.");
        }

        BigDecimal totalValue = price.multiply(new BigDecimal(request.quantity()));
        log.debug("Total value for SELL: {}", totalValue);

        int quantityAfterSell = holding.getQuantity() - request.quantity();
        holding.setQuantity(quantityAfterSell);
        holdingRepository.save(holding);

        participant.setCashBalance(participant.getCashBalance().add(totalValue));
        participantRepository.save(participant);
        log.info("Updated participant cash balance after SELL: participantId={}, newBalance={}", participant.getId(), participant.getCashBalance());

        Transaction transaction = Transaction.builder()
                .holding(holding)
                .transactionType(TransactionType.SELL)
                .quantity(request.quantity())
                .pricePerShare(price)
                .build();

        transactionRepository.save(transaction);
        log.info("Completed SELL transaction: participantId={}, symbol={}, quantity={}, price={}",
                participant.getId(), request.stockSymbol(), request.quantity(), price);
    }

    @Override
    public Portfolio getPortfolio(UUID contestId, UUID userId) {
        log.debug("Fetching portfolio for contestId={}, userId={}", contestId, userId);

        ContestParticipant participant = participantRepository.findByContestIdAndUserId(contestId, userId)
                .orElseThrow(() -> {
                    log.warn("Participant not found for contestId={}, userId={}", contestId, userId);
                    return new ParticipantNotFoundException("Participant not found in the contest.");
                });

        List<PortfolioHolding> holdings = holdingRepository.findByParticipantId(participant.getId());

        Map<String, QuoteResponse> quotes = Flux.fromIterable(holdings)
                .flatMap(holding -> {
                    log.debug("Fetching quote for symbol={}", holding.getStockSymbol());
                    return marketDataClient.getQuote(holding.getStockSymbol());
                })
                .collect(Collectors.toMap(QuoteResponse::symbol, quote -> quote))
                .block();

        Portfolio portfolio = portfolioResponseConverter.convert(participant, holdings, quotes);
        log.info("Fetched Portfolio for participantId={}", participant.getId());

        return portfolio;
    }
}