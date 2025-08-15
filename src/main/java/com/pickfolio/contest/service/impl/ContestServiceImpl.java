package com.pickfolio.contest.service.impl;

import com.pickfolio.contest.converter.ContestResponseConverter;
import com.pickfolio.contest.constant.ContestStatus;
import com.pickfolio.contest.domain.model.Contest;
import com.pickfolio.contest.domain.model.ContestParticipant;
import com.pickfolio.contest.domain.request.CreateContestRequest;
import com.pickfolio.contest.domain.request.JoinContestRequest;
import com.pickfolio.contest.domain.response.ContestResponse;
import com.pickfolio.contest.exception.*;
import com.pickfolio.contest.repository.ContestParticipantRepository;
import com.pickfolio.contest.repository.ContestRepository;
import com.pickfolio.contest.service.ContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContestServiceImpl implements ContestService {

    private final ContestRepository contestRepository;
    private final ContestParticipantRepository contestParticipantRepository;
    private final ContestResponseConverter converter;

    @Override
    @Transactional
    public ContestResponse createContest(final CreateContestRequest request, final UUID creatorId) {
        validateCreateRequest(request);

        Contest contest = Contest.builder()
                .name(request.name())
                .status(ContestStatus.OPEN)
                .isPrivate(request.isPrivate())
                .inviteCode(request.isPrivate() ? generateInviteCode() : null)
                .startTime(request.startTime())
                .endTime(request.endTime())
                .virtualBudget(request.virtualBudget())
                .maxParticipants(request.maxParticipants())
                .creatorId(creatorId)
                .build();

        Contest savedContest = contestRepository.save(contest);

        return converter.convert(savedContest);
    }

    private String generateInviteCode() {
        // A simple, non-guaranteed unique code for the MVP.
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private void validateCreateRequest(CreateContestRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new ContestCreationException("Contest name cannot be empty.");
        }
        if (request.startTime() == null || request.endTime() == null) {
            throw new ContestCreationException("Contest start and end times must be specified.");
        }
        if (request.startTime().isBefore(LocalDateTime.now())) {
            throw new ContestCreationException("Contest start time must be in the future.");
        }
        if (request.endTime().isBefore(request.startTime())) {
            throw new ContestCreationException("Contest end time must be after the start time.");
        }
        if (request.maxParticipants() < 2) {
            throw new ContestCreationException("Contest must allow at least 2 participants.");
        }
        if (request.virtualBudget().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ContestCreationException("Virtual budget must be greater than zero.");
        }
    }


    @Override
    public ContestResponse getContestDetails(UUID contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ContestNotFoundException("Contest not found with ID: " + contestId));

        return converter.convert(contest);
    }

    @Override
    public List<ContestResponse> findOpenPublicContests() {
        List<Contest> openContests = contestRepository.findByStatusAndIsPrivateFalse(ContestStatus.OPEN);

        return openContests.stream()
                .map(converter::convert)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void joinContest(JoinContestRequest request, UUID userId) {
        Contest contest = contestRepository.findById(request.contestId())
                .orElseThrow(() -> new ContestNotFoundException("Contest not found with ID: " + request.contestId()));

        if (contest.getStatus() != ContestStatus.OPEN) {
            throw new ContestNotOpenException("Contest is not open for joining.");
        }

        if (contestParticipantRepository.findByContestIdAndUserId(contest.getId(), userId).isPresent()) {
            throw new UserAlreadyInContestException("User has already joined this contest.");
        }

        if (contestParticipantRepository.countByContestId(contest.getId()) >= contest.getMaxParticipants()) {
            throw new ContestFullException("Contest is already full.");
        }

        if (contest.isPrivate()) {
            if (request.inviteCode() == null || !request.inviteCode().equals(contest.getInviteCode())) {
                throw new InvalidInviteCodeException("Invalid invite code for this private contest.");
            }
        }

        ContestParticipant participant = ContestParticipant.builder()
                .contest(contest)
                .userId(userId)
                .cashBalance(contest.getVirtualBudget())
                .totalPortfolioValue(contest.getVirtualBudget())
                .build();

        contestParticipantRepository.save(participant);
    }
}
