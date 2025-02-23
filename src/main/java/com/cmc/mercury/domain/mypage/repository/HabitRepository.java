package com.cmc.mercury.domain.mypage.repository;

import com.cmc.mercury.domain.mypage.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {

    Optional<Habit> findByUser_Id(Long userId);
}
