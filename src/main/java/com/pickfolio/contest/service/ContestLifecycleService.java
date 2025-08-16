package com.pickfolio.contest.service;

import com.pickfolio.contest.constant.ContestStatus;
import com.pickfolio.contest.domain.model.Contest;
import com.pickfolio.contest.repository.ContestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContestLifecycleService {

    private final ContestRepository contestRepository;

    // This method will run every minute
    @Scheduled(cron = "0 * * * * *") // Runs at the start of every minute
    @Transactional
    public void updateContestStatuses() {
        log.info("Running contest status update job...");
        startPendingContests();
        endLiveContests();
        log.info("Contest status update job finished.");
    }

    private void startPendingContests() {
        List<Contest> contestsToStart = contestRepository.findAllByStatusAndStartTimeBefore(
                ContestStatus.OPEN,
                LocalDateTime.now()
        );

        for (Contest contest : contestsToStart) {
            contest.setStatus(ContestStatus.LIVE);
            contestRepository.save(contest);
            log.info("Contest '{}' (ID: {}) has started.", contest.getName(), contest.getId());
        }
    }

    private void endLiveContests() {
        List<Contest> contestsToEnd = contestRepository.findAllByStatusAndEndTimeBefore(
                ContestStatus.LIVE,
                LocalDateTime.now()
        );

        for (Contest contest : contestsToEnd) {
            contest.setStatus(ContestStatus.COMPLETED);
            contestRepository.save(contest);
            log.info("Contest '{}' (ID: {}) has ended.", contest.getName(), contest.getId());
        }
    }
}