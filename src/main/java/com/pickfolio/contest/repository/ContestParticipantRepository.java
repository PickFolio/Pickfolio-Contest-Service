package com.pickfolio.contest.repository;

import com.pickfolio.contest.domain.model.ContestParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContestParticipantRepository extends JpaRepository<ContestParticipant, UUID> {

    // Check if a user has already joined a specific contest
    Optional<ContestParticipant> findByContestIdAndUserId(UUID contestId, UUID userId);
}