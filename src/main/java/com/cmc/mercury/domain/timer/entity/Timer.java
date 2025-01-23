package com.cmc.mercury.domain.timer.entity;

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
public class Timer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int seconds;

    @Column(nullable = false)
    private int acquiredExp;

    @Builder
    public Timer(User user, int seconds, int acquiredExp) {
        this.user = user;
        this.seconds = seconds;
        this.acquiredExp = acquiredExp;
    }
}
