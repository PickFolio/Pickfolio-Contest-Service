package com.pickfolio.contest.converter;

import com.pickfolio.contest.domain.model.Contest;
import com.pickfolio.contest.domain.response.ContestResponse;
import com.pickfolio.contest.repository.ContestParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContestResponseConverter implements Converter<Contest, ContestResponse> {

    private final ContestParticipantRepository participantRepository;

    @Override
    public ContestResponse convert(Contest contest) {

        int currentParticipants = participantRepository.countByContestId(contest.getId());

        return new ContestResponse(
                contest.getId(),
                contest.getName(),
                contest.getStatus(),
                contest.isPrivate(),
                contest.getInviteCode(),
                contest.getStartTime(),
                contest.getEndTime(),
                contest.getVirtualBudget(),
                contest.getMaxParticipants(),
                currentParticipants,
                contest.getCreatorId(),
                contest.getCreatedAt()
        );
    }
}