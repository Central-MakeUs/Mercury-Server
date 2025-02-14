package com.cmc.mercury.global.oauth.handler;

import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.domain.user.repository.UserRepository;
import com.cmc.mercury.global.exception.CustomException;
import com.cmc.mercury.global.exception.ErrorCode;
import com.cmc.mercury.global.jwt.JwtProvider;
import com.cmc.mercury.global.oauth.userinfo.OAuth2UserInfo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("OAuth2 Login 성공!");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // 인증이 완료된 후 새로운 요청이 발생하면 request에 저장된 데이터(isNewUser). 는 사라짐
        boolean isNewUser = (boolean) oAuth2User.getAttributes().getOrDefault("isNewUser", false);

        // oauthId로 사용자 조회
        User user = userRepository.findByOauthId(oAuth2User.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // JWT 토큰 생성
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), user.getEmail());
        log.info("accessToken, refreshToken: {}, {}", accessToken, refreshToken);

        // Access Token은 Authorization 헤더에 추가
        response.setHeader("Authorization", "Bearer " + accessToken);
        log.info("Header에 설정은 성공");

        // Refresh Token은 보안을 위해 HttpOnly 쿠키로 설정
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true); // JavaScript에서 접근 방지
        refreshTokenCookie.setSecure(true); // HTTPS만 허용
        refreshTokenCookie.setPath("/"); // 모든 경로에서 접근 가능
        refreshTokenCookie.setDomain("mercuryplanet.co.kr");  // 도메인 간 쿠키 공유
        refreshTokenCookie.setAttribute("SameSite", "None");
        refreshTokenCookie.setMaxAge((int) refreshTokenValidity / 1000); // ms를 초 단위로 변환
        response.addCookie(refreshTokenCookie);

        // 리다이렉트 URL에 토큰 포함하여 이동
        String targetUrl = UriComponentsBuilder.fromUriString("https://www.mercuryplanet.co.kr/login/success")
                .queryParam("access_token", accessToken)
                .queryParam("isNewUser", isNewUser)
                .build(true).toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}