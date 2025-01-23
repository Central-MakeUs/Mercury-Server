package com.cmc.mercury.domain.timer.service;

import com.cmc.mercury.domain.timer.dto.TimerListResponse;
import com.cmc.mercury.domain.timer.dto.TimerRequest;
import com.cmc.mercury.domain.timer.dto.TimerResponse;
import com.cmc.mercury.domain.timer.entity.Timer;
import com.cmc.mercury.domain.timer.repository.TimerRepository;
import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.domain.user.service.UserTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimerService {

    private final TimerRepository timerRepository;
    private final UserTestService userTestService;

    private static final int MIN_EXP_MINUTES = 5;
    private static final int MAX_EXP_MINUTES = 25;

    @Transactional
    public TimerResponse createTimer(Long testUserId, TimerRequest request) {

        User user = userTestService.getOrCreateTestUser(testUserId);

        int exp = calculateExp(request.seconds());

        Timer timer = Timer.builder()
                .user(user)
                .seconds(request.seconds())
                .acquiredExp(exp)
                .build();

        Timer savedTimer = timerRepository.save(timer);

        return TimerResponse.of(savedTimer);
    }

    public TimerListResponse getTimerList(Long testUserId) {

        List<Timer> timers = timerRepository.findAllByUser_testUserId(testUserId);

        List<TimerResponse> timerResponses = timers.stream()
                .map(TimerResponse::of)
                .toList();

        return new TimerListResponse(timerResponses);
    }

    // 타이머 기록 시간에 따라 경험치 계산
    private int calculateExp(int seconds) {
        int minutes = seconds / 60;

        if (minutes < 5) return 0;
        if (minutes > 25) return 25;
        return minutes;
    }
}
