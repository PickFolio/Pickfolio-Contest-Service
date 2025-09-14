package com.pickfolio.contest.repository;

import com.pickfolio.contest.domain.model.ContestParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContestParticipantRepository extends JpaRepository<ContestParticipant, UUID> {

    // Check if a user has already joined a specific contest
    Optional<ContestParticipant> findByContestIdAndUserId(UUID contestId, UUID userId);

    Integer countByContestId(UUID contestId);

    @Query("SELECT p FROM ContestParticipant p JOIN FETCH p.holdings WHERE p.contest.status = 'LIVE'")
    List<ContestParticipant> findAllWithHoldingsInLiveContests();
}