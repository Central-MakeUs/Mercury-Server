package com.cmc.mercury.domain.timer.repository;

import com.cmc.mercury.domain.timer.entity.Timer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimerRepository extends JpaRepository<Timer, Long> {

    List<Timer> findAllByUser_testUserId(Long testUserId);
}
