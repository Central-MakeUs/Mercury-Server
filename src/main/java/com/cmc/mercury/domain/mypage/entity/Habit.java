package com.cmc.mercury.domain.mypage.entity;

import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Habit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "habit_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int streakDays;

    @Builder
    public Habit(User user, int streakDays) {
        this.user = user;
        this.streakDays = streakDays;
    }

    public void updateStreakDays(int streakDays) {
        this.streakDays = streakDays;
    }
}
