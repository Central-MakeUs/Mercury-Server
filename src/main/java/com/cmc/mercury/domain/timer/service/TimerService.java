package com.cmc.mercury.domain.timer.service;

import com.cmc.mercury.domain.mypage.entity.HabitHistory;
import com.cmc.mercury.domain.mypage.repository.HabitHistoryRepository;
import com.cmc.mercury.domain.mypage.service.MyPageService;
import com.cmc.mercury.domain.timer.dto.TimerListResponse;
import com.cmc.mercury.domain.timer.dto.TimerRequest;
import com.cmc.mercury.domain.timer.dto.TimerResponse;
import com.cmc.mercury.domain.timer.entity.Timer;
import com.cmc.mercury.domain.timer.repository.TimerRepository;
import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimerService {

    private final TimerRepository timerRepository;
    private final HabitHistoryRepository habitHistoryRepository;
    private final MyPageService myPageService;

    private static final int MAX_EXP_SECONDS = 1800;
    private static final int DAILY_EXP_LIMIT = 180;


    @Transactional
    public TimerResponse createTimer(User user, TimerRequest request) {

        int todayTotalExp = calculateTodayTotalExp(user.getId(), request.deviceTime());

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

        // 날짜별 습관쌓기 기록
        if (request.seconds() >= 10) {
            LocalDate date = LocalDate.now();
            HabitHistory history = myPageService.saveHabitHistoryIfNotExists(user.getId(), date, newExp);
            history.updateHasTimer();
            habitHistoryRepository.save(history);
        }

        return TimerResponse.of(savedTimer);
    }

    public TimerListResponse getTimerList(User user) {

        List<Timer> timers = timerRepository.findAllByUser_Id(user.getId());

        List<TimerResponse> timerResponses = timers.stream()
                .map(TimerResponse::of)
                .toList();

        return new TimerListResponse(timerResponses);
    }

    // 타이머 기록 시간에 따라 경험치 계산
    private int calculateExp(int seconds) {
        int exp = seconds / 10;

        if (exp > MAX_EXP_SECONDS) return DAILY_EXP_LIMIT;
        return exp;
    }

    // 오늘 획득한 총 경험치 계산
    private int calculateTodayTotalExp(Long userId, LocalDateTime deviceTime) {

        LocalDateTime startOfDay = deviceTime.toLocalDate().atStartOfDay();  // 오늘 하루(일일)의 기준은 device time으로 00시 00분 자정

        return timerRepository.findAllByUser_IdAndCreatedAtBetween(
                        userId,
                        startOfDay,
                        deviceTime
                ).stream()
                .mapToInt(Timer::getAcquiredExp)
                .sum();
    }
}
