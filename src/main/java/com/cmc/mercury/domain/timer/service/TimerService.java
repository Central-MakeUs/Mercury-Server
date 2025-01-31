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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimerService {

    private final TimerRepository timerRepository;
    private final UserTestService userTestService;

    private static final int MIN_EXP_MINUTES = 5;
    private static final int MAX_EXP_MINUTES = 25;
    private static final int DAILY_EXP_LIMIT = 120;


    @Transactional
    public TimerResponse createTimer(Long testUserId, TimerRequest request) {

        User user = userTestService.getOrCreateTestUser(testUserId);

        int todayTotalExp = calculateTodayTotalExp(testUserId, request.deviceTime());

        int newExp = calculateExp(request.seconds());

        // 일일 한도 체크 및 조정
        if (todayTotalExp + newExp > DAILY_EXP_LIMIT) {
            newExp = Math.max(0, DAILY_EXP_LIMIT - todayTotalExp);
        }

        Timer timer = Timer.builder()
                .user(user)
                .seconds(request.seconds())
                .acquiredExp(newExp)
                .build();

        Timer savedTimer = timerRepository.save(timer);

        // User의 총 경험치 업데이트
        user.updateExp(user.getExp() + newExp);

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

        if (minutes < MIN_EXP_MINUTES) return 0;
        if (minutes > MAX_EXP_MINUTES) return 25;
        return minutes;
    }

    // 오늘 획득한 총 경험치 계산
    private int calculateTodayTotalExp(Long testUserId, LocalDateTime deviceTime) {

        LocalDateTime startOfDay = deviceTime.toLocalDate().atStartOfDay();  // 오늘 하루(일일)의 기준은 device time으로 00시 00분 자정

        return timerRepository.findAllByUser_testUserIdAndCreatedAtBetween(
                        testUserId,
                        startOfDay,
                        deviceTime
                ).stream()
                .mapToInt(Timer::getAcquiredExp)
                .sum();
    }
}
