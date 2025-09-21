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
import com.pickfolio.contest.repository.PortfolioHoldingRepository;
import com.pickfolio.contest.service.ContestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContestServiceImpl implements ContestService {

    private final ContestRepository contestRepository;
    private final ContestParticipantRepository contestParticipantRepository;
    private final PortfolioHoldingRepository portfolioHoldingRepository;
    private final ContestResponseConverter converter;

    @Override
    @Transactional
    public ContestResponse createContest(final CreateContestRequest request, final UUID creatorId) {
        validateCreateRequest(request);

        log.info("Creating contest with name: {}, creatorId: {}", request.name(), creatorId);
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
        log.info("Contest created with ID: {}", savedContest.getId());

        // Automatically join the creator to the contest they just created.
        ContestParticipant creatorAsParticipant = ContestParticipant.builder()
                .contest(savedContest)
                .userId(creatorId)
                .cashBalance(savedContest.getVirtualBudget())
                .totalPortfolioValue(savedContest.getVirtualBudget())
                .build();

        contestParticipantRepository.save(creatorAsParticipant);
        log.info("Creator {} automatically joined contest {}", creatorId, savedContest.getId());

        return converter.convert(savedContest);
    }

    private String generateInviteCode() {
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        log.debug("Generated invite code: {}", code);
        return code;
    }

    private void validateCreateRequest(CreateContestRequest request) {
        log.debug("Validating create contest request: {}", request);
        if (request.name() == null || request.name().isBlank()) {
            log.warn("Contest name is empty");
            throw new ContestCreationException("Contest name cannot be empty.");
        }
        if (request.startTime() == null || request.endTime() == null) {
            log.warn("Contest start or end time is not specified");
            throw new ContestCreationException("Contest start and end times must be specified.");
        }
        if (request.startTime().toInstant(ZoneOffset.UTC).isBefore(Instant.now())) {
            log.warn("Contest start time is in the past: {}", request.startTime());
            throw new ContestCreationException("Contest start time must be in the future.");
        }
        if (request.endTime().isBefore(request.startTime())) {
            log.warn("Contest end time is before start time: start={}, end={}", request.startTime(), request.endTime());
            throw new ContestCreationException("Contest end time must be after the start time.");
        }
        if (request.maxParticipants() < 2) {
            log.warn("Contest max participants less than 2: {}", request.maxParticipants());
            throw new ContestCreationException("Contest must allow at least 2 participants.");
        }
        if (request.virtualBudget().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Contest virtual budget not greater than zero: {}", request.virtualBudget());
            throw new ContestCreationException("Virtual budget must be greater than zero.");
        }
    }

    @Override
    public ContestResponse getContestDetails(UUID contestId) {
        //TODO: Contest details to include participants, scores, etc.
        log.info("Fetching contest details for ID: {}", contestId);
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> {
                    log.warn("Contest not found with id: {}", contestId);
                    return new ContestNotFoundException("Contest not found with ID: " + contestId);
                });

        return converter.convert(contest);
    }

    @Override
    public List<ContestResponse> findOpenPublicContests() {
        log.info("Finding open public contests");
        List<Contest> openContests = contestRepository.findByStatusAndIsPrivateFalse(ContestStatus.OPEN);

        log.debug("Found {} open public contests", openContests.size());
        return openContests.stream()
                .map(converter::convert)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void joinContest(JoinContestRequest request, UUID userId) {
        log.info("User {} joining contest {}", userId, request.contestId());
        Contest contest = contestRepository.findById(request.contestId())
                .orElseThrow(() -> {
                    log.warn("Contest not found: {}", request.contestId());
                    return new ContestNotFoundException("Contest not found with ID: " + request.contestId());
                });

        if (contest.getStatus() != ContestStatus.OPEN) {
            log.warn("Contest {} is not open for joining", contest.getId());
            throw new ContestNotOpenException("Contest is not open for joining.");
        }

        if (contestParticipantRepository.findByContestIdAndUserId(contest.getId(), userId).isPresent()) {
            log.warn("User {} already joined contest {}", userId, contest.getId());
            throw new UserAlreadyInContestException("User has already joined this contest.");
        }

        if (contestParticipantRepository.countByContestId(contest.getId()) >= contest.getMaxParticipants()) {
            log.warn("Contest {} is full", contest.getId());
            throw new ContestFullException("Contest is already full.");
        }

        if (contest.isPrivate()) {
            if (request.inviteCode() == null || !request.inviteCode().equals(contest.getInviteCode())) {
                log.warn("Invalid invite code for contest {}: provided={}, expected={}", contest.getId(), request.inviteCode(), contest.getInviteCode());
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
        log.info("User {} joined contest {}", userId, contest.getId());
    }

    @Override
    public List<String> findActiveSymbols() {
        List<String> symbols = portfolioHoldingRepository.findDistinctStockSymbolsInLiveContests();
        log.debug("Found {} active symbols in live contests", symbols.size());
        return symbols;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContestResponse> findMyContests(UUID userId) {
        log.info("Finding all contests for user {}", userId);

        List<Contest> createdContests = contestRepository.findAllByCreatorId(userId);

        List<ContestParticipant> participants = contestParticipantRepository.findAllByUserId(userId);
        List<Contest> joinedContests = participants.stream()
                .map(ContestParticipant::getContest)
                .toList();

        return Stream.concat(createdContests.stream(), joinedContests.stream())
                .distinct()
                .sorted(Comparator.comparing(Contest::getCreatedAt).reversed())
                .map(converter::convert)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void joinContestByInviteCode(JoinContestRequest request, UUID userId) {
        log.info("User {} joining contest with invite code: {}", userId, request.inviteCode());
        Contest contest = contestRepository.findByInviteCode(request.inviteCode())
                .orElseThrow(() -> {
                    log.warn("No contest found with invite code: {}", request.inviteCode());
                    return new ContestNotFoundException("Contest not found with ID: " + request.contestId());
                });

        if (contest.getStatus() != ContestStatus.OPEN) {
            log.warn("Private contest {} is not open for joining", contest.getId());
            throw new ContestNotOpenException("Contest is not open for joining.");
        }

        if (contestParticipantRepository.findByContestIdAndUserId(contest.getId(), userId).isPresent()) {
            log.warn("User {} already joined the private contest {}", userId, contest.getId());
            throw new UserAlreadyInContestException("User has already joined this contest.");
        }

        if (contestParticipantRepository.countByContestId(contest.getId()) >= contest.getMaxParticipants()) {
            log.warn("Private contest {} is full", contest.getId());
            throw new ContestFullException("Contest is already full.");
        }

        ContestParticipant participant = ContestParticipant.builder()
                .contest(contest)
                .userId(userId)
                .cashBalance(contest.getVirtualBudget())
                .totalPortfolioValue(contest.getVirtualBudget())
                .build();

        contestParticipantRepository.save(participant);
        log.info("User {} joined private contest {}", userId, contest.getId());
    }
}