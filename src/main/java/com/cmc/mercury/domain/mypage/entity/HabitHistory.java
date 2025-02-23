package com.cmc.mercury.domain.mypage.entity;

import com.cmc.mercury.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class HabitHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "habit_history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id")
    private Habit habit;

    @Column(nullable = false)
    private boolean hasRecord = false;

    @Column(nullable = false)
    private boolean hasTimer = false;

    @Column(nullable = false)
    private int streakCount = 0;

    @Column(nullable = false)
    private int acquiredExp = 0;

    @Builder
    public HabitHistory(Habit habit, Integer streakCount, Integer acquiredExp, Boolean hasRecord, Boolean hasTimer) {
        this.habit = habit;
        this.streakCount = (streakCount != null) ? streakCount : 0;
        this.acquiredExp = (acquiredExp != null) ? acquiredExp : 0;
        this.hasRecord = (hasRecord != null) ? hasRecord : false;
        this.hasTimer = (hasTimer != null) ? hasTimer : false;
    }

    public void updateHasRecord() {
        this.hasRecord = true;
    }

    public void updateHasTimer() {
        this.hasTimer = true;
    }

    public void updateAcquiredExp(int acquiredExp) {
        this.acquiredExp += acquiredExp;
    }

}
