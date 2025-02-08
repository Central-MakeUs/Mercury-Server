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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;

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

        // isShortLivedAccessToken 옵션에 따라 유효기간 선택
        long tokenValidity = request.isShortLivedAccessToken()
                ? 20000 // 20초
                : accessTokenValidity;

        // access token만 발급
        String accessToken = jwtProvider.createToken(user.getId(), user.getEmail(), "AccessToken", tokenValidity);

        // 헤더에 access token 설정
        response.setHeader("Authorization", "Bearer " + accessToken);

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

        // 토큰 재발급
        long tokenValidity = request.isShortLivedAccessToken()
                ? 20000  // 20초
                : accessTokenValidity;

        String accessToken = jwtProvider.createToken(user.getId(), user.getEmail(), "AccessToken", tokenValidity);
        response.setHeader("Authorization", "Bearer " + accessToken);

        return user;
    }

    public List<User> getListUsers() {
        return userRepository.findAll();
    }
}
