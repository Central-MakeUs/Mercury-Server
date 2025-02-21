package com.cmc.mercury.domain.user.entity;

import com.cmc.mercury.global.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

//    @Column(nullable = false)
//    @Column(length = 30) // DB 컬럼 길이 제한 -> 바이트 단위라 한글을 위해 여유 있게
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private int exp;

//    @Column(unique = true)
//    private Long testUserId;  // 테스트용 ID 필드

//    private String password; // 소셜 로그인 제공자가 password를 제공하지 않을 수 있으므로 null 허용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OAuthType oauthType;

    @Column(nullable = false)
    private String oauthId;

    private String refreshToken; // 최초 로그인 시에는 값이 없으므로 null 허용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus userStatus;

    @Builder
    public User(String email, OAuthType oauthType,
                String oauthId, UserStatus userStatus) {
        this.email = email;
        this.nickname = "Mercury";
        this.oauthType = oauthType;
        this.oauthId = oauthId;
        this.userStatus = userStatus;
        this.exp = 0; // 초기 경험치는 0
    }

    // 게스트용 생성자
    @Builder(builderMethodName = "TestUserBuilder", buildMethodName = "TestUserBuild")
    public User(String email) {
        this.email = email;
        this.nickname = "TestUser";
        this.oauthType = OAuthType.TEST;
        this.oauthId = UUID.randomUUID().toString();;
        this.userStatus = UserStatus.ACTIVE;
        this.exp = 0;  // 초기 경험치는 0
    }

    public void updateExp(int exp) {
        this.exp = exp;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }
}
