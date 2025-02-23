package com.cmc.mercury.domain.mypage.service;

import com.cmc.mercury.domain.mypage.dto.HabitDetailResponse;
import com.cmc.mercury.domain.mypage.dto.MyPageResponse;
import com.cmc.mercury.domain.mypage.dto.WeeklyStreakResponse;
import com.cmc.mercury.domain.mypage.entity.Habit;
import com.cmc.mercury.domain.mypage.entity.HabitHistory;
import com.cmc.mercury.domain.mypage.repository.HabitHistoryRepository;
import com.cmc.mercury.domain.mypage.repository.HabitRepository;
import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.global.exception.CustomException;
import com.cmc.mercury.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyPageService {

    private final HabitRepository habitRepository;
    private final HabitHistoryRepository habitHistoryRepository;

    public MyPageResponse getMyPage(User user) {

        Habit habit = habitRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.HABIT_NOT_FOUND));

        LocalDate today = LocalDate.now();
        LocalDateTime startOfWeek = today.with(DayOfWeek.MONDAY).atStartOfDay(); // 이번 주 월요일 00:00:00
        LocalDateTime endOfWeek = today.with(DayOfWeek.SUNDAY).atTime(LocalTime.MAX); // 이번 주 일요일 23:59:59
        List<HabitHistory> histories = habitHistoryRepository.findThisWeekByHabitId(habit.getId(), startOfWeek, endOfWeek); // 이번 주의 기록

        // Map을 사용하여 빠른 조회
        Map<DayOfWeek, HabitHistory> historyMap = histories.stream()
                .collect(Collectors.toMap(h -> h.getCreatedAt().getDayOfWeek(), h -> h));

        List<WeeklyStreakResponse> weeklyStreak = Arrays.stream(DayOfWeek.values())
                .map(day -> new WeeklyStreakResponse(
                        day.name(),
                        historyMap.get(day) != null && historyMap.get(day).getStreakCount() > 0
                ))
                .collect(Collectors.toList());

        return new MyPageResponse(
                habit.getId(),
                getJoinDays(user.getCreatedAt()), // 가입한 지 며칠인지 계산
                user.getNickname(),
                user.getExp(),
                habit.getStreakDays(),
                weeklyStreak
        );
    }

    public HabitDetailResponse getHabitDetail(User user, LocalDate date) {

        LocalDateTime startOfDay = date.atStartOfDay(); // 00:00:00
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX); // 23:59:59

        // 오늘 streak 확인
        HabitHistory history = habitHistoryRepository
                .findByHabit_User_IdAndCreatedAt(user.getId(), startOfDay, endOfDay).orElse(null);

        // 어제 streakCount 확인
        LocalDateTime yesterdayStart = startOfDay.minusDays(1);
        LocalDateTime yesterdayEnd = endOfDay.minusDays(1);
        Optional<HabitHistory> yesterdayHistory = habitHistoryRepository.findByHabit_User_IdAndCreatedAt(user.getId(), yesterdayStart, yesterdayEnd);
        int previousStreakCount = yesterdayHistory.map(HabitHistory::getStreakCount).orElse(0);

        return new HabitDetailResponse(
                history != null ? history.getId() : null,  // 기록이 없으면 null
                date.getDayOfWeek().name(),
                history != null ? history.getStreakCount() : previousStreakCount,
                history != null ? history.getAcquiredExp() : 0,
                history != null && history.isHasRecord(),
                history != null && history.isHasTimer()
        );
    }

    private int getJoinDays(LocalDateTime joinDate) { // 가입 기간 계산

        return (int) ChronoUnit.DAYS.between(joinDate.toLocalDate(), LocalDate.now()) + 1;
    }

    // 해당 날짜에 HabitHistory가 없으면 생성
    public HabitHistory saveHabitHistoryIfNotExists(Long userId, LocalDate date, int acquiredExp) {

        // 오늘 기록이 이미 존재하는지 확인
        LocalDateTime startOfDay = date.atStartOfDay(); // 00:00:00
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX); // 23:59:59
        Optional<HabitHistory> existingHistory = habitHistoryRepository.findByHabit_User_IdAndCreatedAt(userId, startOfDay, endOfDay);

        if (existingHistory.isPresent()) {
            // 이미 존재하면 acquiredExp를 증가시키고 반환
            HabitHistory history = existingHistory.get();
            history.updateAcquiredExp(acquiredExp);
            return habitHistoryRepository.save(history);
        }

        // 어제의 streak 확인 (어제 streak이 연속되었는지 확인)
        LocalDateTime yesterdayStart = startOfDay.minusDays(1);
        LocalDateTime yesterdayEnd = endOfDay.minusDays(1);
        Optional<HabitHistory> yesterdayHistory = habitHistoryRepository.findByHabit_User_IdAndCreatedAt(userId, yesterdayStart, yesterdayEnd);

        int newStreakCount = (yesterdayHistory.isPresent() && yesterdayHistory.get().getStreakCount() > 0)
                ? yesterdayHistory.get().getStreakCount() + 1 // 연속 streak이면 +1
                : 1; // 처음 streak 시작

        // 새로운 오늘의 기록
        Habit habit = habitRepository.findByUser_Id(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.HABIT_NOT_FOUND));

        // habit.streakDays도 함께 갱신
        habit.updateStreakDays(newStreakCount);
        habitRepository.save(habit);

        HabitHistory newHistory = HabitHistory.builder()
                .habit(habit)
                .streakCount(newStreakCount)
                .acquiredExp(acquiredExp)
                .hasRecord(false)
                .hasTimer(false)
                .build();

        return habitHistoryRepository.save(newHistory);
    }
}
