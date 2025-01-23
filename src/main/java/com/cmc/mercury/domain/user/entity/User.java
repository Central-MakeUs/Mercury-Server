package com.cmc.mercury.domain.user.entity;

import com.cmc.mercury.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private int exp;

    @Column(unique = true)
    private Long testUserId;  // 테스트용 ID 필드

    // 테스트용 생성자
    @Builder
    public User(Long testUserId) {
        this.testUserId = testUserId;
        this.nickname = "TestUser_" + testUserId;
        this.email = "test" + testUserId + "@test.com";
        this.exp = 0;  // 초기 경험치는 0
    }

    public void updateExp(int exp) {
        this.exp = exp;
    }
}
