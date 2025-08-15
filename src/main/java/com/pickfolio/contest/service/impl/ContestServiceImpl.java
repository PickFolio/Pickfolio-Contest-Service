package com.pickfolio.contest.service.impl;

import com.pickfolio.contest.converter.ContestResponseConverter;
import com.pickfolio.contest.constant.ContestStatus;
import com.pickfolio.contest.domain.model.Contest;
import com.pickfolio.contest.domain.request.CreateContestRequest;
import com.pickfolio.contest.domain.request.JoinContestRequest;
import com.pickfolio.contest.domain.response.ContestResponse;
import com.pickfolio.contest.repository.ContestRepository;
import com.pickfolio.contest.service.ContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContestServiceImpl implements ContestService {

    private final ContestRepository contestRepository;
    private final ContestResponseConverter converter;

    @Override
    @Transactional
    public ContestResponse createContest(CreateContestRequest request, UUID creatorId) {
        // TODO: Add validation: endTime must be after startTime, budget > 0, etc.

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


    @Override
    public ContestResponse getContestDetails(UUID contestId) {
        return null;
    }

    @Override
    public List<ContestResponse> findOpenPublicContests() {
        return List.of();
    }

    @Override
    public void joinContest(JoinContestRequest request, UUID userId) {

    }
}
