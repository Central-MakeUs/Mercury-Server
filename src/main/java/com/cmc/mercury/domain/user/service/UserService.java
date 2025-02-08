package com.cmc.mercury.domain.user.service;

import com.cmc.mercury.domain.user.entity.OAuthType;
import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.domain.user.repository.UserRepository;
import com.cmc.mercury.domain.user.response.UserTestRequest;
import com.cmc.mercury.global.exception.CustomException;
import com.cmc.mercury.global.exception.ErrorCode;
import com.cmc.mercury.global.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final HttpServletResponse response;

    @Transactional
    public User createTestUser(UserTestRequest request) {

        // 이메일과 OAuthType으로 중복 검증
        if (userRepository.existsByEmailAndOauthType(request.email(), OAuthType.TEST)) {
            throw new CustomException(ErrorCode.DUPLICATE_USER);
        }

        User user = User.TestUserBuilder()
                        .email(request.email())
                        .TestUserBuild();

        User savedUser = userRepository.save(user);

        setTestUserTokens(savedUser, request.isShortLivedAccessToken());

        return savedUser;
    }

    @Transactional
    public void deleteTestUser(String email) {

        User user = userRepository.findByEmailAndOauthType(email, OAuthType.TEST)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
    }

    @Transactional
    public User refreshTestToken(UserTestRequest request) {

        // 해당 이메일의 TEST 계정 찾기
        User user = userRepository.findByEmailAndOauthType(request.email(), OAuthType.TEST)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        setTestUserTokens(user, request.isShortLivedAccessToken());

        return user;
    }

    private void setTestUserTokens(User user, boolean isShortLivedAccessToken) {
        // Access Token 생성
        long tokenValidity = isShortLivedAccessToken
                ? 20000  // 20초
                : accessTokenValidity;

        String accessToken = jwtProvider.createToken(user.getId(), user.getEmail(), "AccessToken", tokenValidity);
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), user.getEmail());

        // 토큰 설정
        response.setHeader("Authorization", "Bearer " + accessToken);

        // Refresh Token 쿠키 설정
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        // refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        // refreshTokenCookie.setDomain("mercuryplanet.co.kr");
        refreshTokenCookie.setMaxAge((int) refreshTokenValidity / 1000);
        response.addCookie(refreshTokenCookie);
    }

        public List<User> getListUsers() {
        return userRepository.findAll();
    }
}
