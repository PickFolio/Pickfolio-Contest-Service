package com.pickfolio.contest.converter;

import com.pickfolio.contest.domain.model.Contest;
import com.pickfolio.contest.domain.response.ContestResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ContestResponseConverter implements Converter<Contest, ContestResponse> {

    @Override
    public ContestResponse convert(Contest contest) {
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
                contest.getCreatorId(),
                contest.getCreatedAt()
        );
    }
}