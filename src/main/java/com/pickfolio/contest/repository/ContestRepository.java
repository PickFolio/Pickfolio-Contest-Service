package com.pickfolio.contest.repository;

import com.pickfolio.contest.domain.model.Contest;
import com.pickfolio.contest.constant.ContestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContestRepository extends JpaRepository<Contest, UUID> {

    // Find contests that are open for joining and are public
    List<Contest> findByStatusAndIsPrivateFalse(ContestStatus status);

    // Find a private contest by its invite code
    Optional<Contest> findByInviteCode(String inviteCode);
}