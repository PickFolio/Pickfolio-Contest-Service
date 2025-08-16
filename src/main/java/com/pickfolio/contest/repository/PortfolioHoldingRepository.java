package com.pickfolio.contest.repository;

import com.pickfolio.contest.domain.model.PortfolioHolding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PortfolioHoldingRepository extends JpaRepository<PortfolioHolding, UUID> {

    // Find a specific stock holding for a participant
    Optional<PortfolioHolding> findByParticipantIdAndStockSymbol(UUID participantId, String stockSymbol);

    List<PortfolioHolding> findByParticipantId(UUID participantId);
}