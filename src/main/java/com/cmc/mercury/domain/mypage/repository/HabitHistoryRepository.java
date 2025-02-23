package com.cmc.mercury.domain.mypage.repository;

import com.cmc.mercury.domain.mypage.entity.HabitHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitHistoryRepository extends JpaRepository<HabitHistory, Long> {

    // 해당 주간의 기록 조회
    @Query("SELECT h FROM HabitHistory h WHERE h.habit.id = :habitId AND h.createdAt BETWEEN :startOfWeek AND :endOfWeek")
    List<HabitHistory> findThisWeekByHabitId(
            @Param("habitId") Long habitId,
            @Param("startOfWeek") LocalDateTime startOfWeek, @Param("endOfWeek") LocalDateTime endOfWeek
    );

    // 날짜별 기록 조회
    @Query("SELECT h FROM HabitHistory h WHERE h.habit.user.id = :userId AND h.createdAt BETWEEN :startOfDay AND :endOfDay")
    Optional<HabitHistory> findByHabit_User_IdAndCreatedAt(
            @Param("userId") Long userId,
            @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay
    );
}
