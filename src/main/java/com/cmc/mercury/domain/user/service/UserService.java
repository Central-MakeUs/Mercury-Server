package com.cmc.mercury.domain.user.service;

import com.cmc.mercury.domain.user.entity.OAuthType;
import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.domain.user.repository.UserRepository;
import com.cmc.mercury.domain.user.response.UserTestRequest;
import com.cmc.mercury.global.exception.CustomException;
import com.cmc.mercury.global.exception.ErrorCode;
import com.cmc.mercury.global.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
/*        if (userRepository.existsByEmailAndOauthType(request.email(), OAuthType.TEST)) {
            throw new CustomException(ErrorCode.DUPLICATE_USER);
        }*/
        User existingUser = userRepository.findByEmailAndOauthType(request.email(), OAuthType.TEST)
                .orElse(null);


        if (existingUser != null) {
            // 기존 유저가 있으면 로그인 처리 (토큰 갱신)
            setTestUserTokens(existingUser, request.isShortLivedAccessToken());
            return existingUser;
        }

        User newUser = User.TestUserBuilder()
                        .email(request.email())
                        .TestUserBuild();

        User savedUser = userRepository.save(newUser);

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
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setDomain("mercuryplanet.co.kr");
        refreshTokenCookie.setAttribute("SameSite", "None");
        refreshTokenCookie.setMaxAge((int) refreshTokenValidity / 1000);
        response.addCookie(refreshTokenCookie);
    }

    public User getUser(String accessToken) {

        // Access Token 검증
        jwtProvider.validateToken(accessToken, "AccessToken");

        // 유효성 검증이 완료된 토큰에서 user 추출
        return jwtProvider.getUserFromToken(accessToken);
    }
}
