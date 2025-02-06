package com.cmc.mercury.global.jwt;

import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.domain.user.repository.UserRepository;
import com.cmc.mercury.global.exception.CustomException;
import com.cmc.mercury.global.exception.ErrorCode;
import com.cmc.mercury.global.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/login") ||
                path.startsWith("/oauth2") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/api-docs") ||
                path.startsWith("/books/search")||
                path.startsWith("/users");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String accessToken = extractAccessToken(request);
            log.info("Access Token: {}", accessToken);

            // Access Token이 없음
            if (!StringUtils.hasText(accessToken)) {
                throw new CustomException(ErrorCode.EMPTY_TOKEN);
            }

            try {
                // Access Token 검증
                jwtProvider.validateToken(accessToken, "AccessToken");

                // 유효성 검증이 완료된 토큰에서 user 추출
                User user = jwtProvider.getUserFromToken(accessToken);

                // Authentication 객체 생성해서 SecurityContext에 저장
                Authentication authentication = createAuthentication(user);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(request, response);

            } catch (CustomException e) {
                // Access Token이 만료된 경우, Refresh Token 확인
                if (e.getErrorCode() == ErrorCode.EXPIRED_TOKEN) {
                    String refreshToken = extractRefreshToken(request);

                    if (StringUtils.hasText(refreshToken)) {
                        // Refresh Token이 유효한지 검증
                        jwtProvider.validateToken(refreshToken, "RefreshToken");
                        // Refresh Token이 DB에 저장된 것과 일치하는지 검증
                        jwtProvider.checkRefreshToken(refreshToken);

                        // Refresh Token이 유효하면 새로운 Access Token과 Refresh Token 발급
                        User user = jwtProvider.getUserFromToken(refreshToken);
                        String newAccessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail());
                        String newRefreshToken = jwtProvider.createRefreshToken(user.getId(), user.getEmail());

                        // 새로운 Access Token을 헤더에 추가
                        response.setHeader("Authorization", "Bearer " + newAccessToken);

                        // 새로운 Refresh Token을 쿠키에 설정
                        Cookie refreshTokenCookie = new Cookie("refresh_token", newRefreshToken);
                        refreshTokenCookie.setHttpOnly(true); // JavaScript에서 접근 방지
                        refreshTokenCookie.setSecure(true); // HTTPS만 허용
                        refreshTokenCookie.setPath("/"); // 모든 경로에서 접근 가능
                        refreshTokenCookie.setDomain("mercuryplanet.co.kr");  // 도메인 간 쿠키 공유
                        refreshTokenCookie.setMaxAge((int) refreshTokenValidity / 1000); // ms를 초 단위로 변환
                        response.addCookie(refreshTokenCookie);

                        // Authentication 객체 생성해서 SecurityContext에 저장
                        Authentication authentication = createAuthentication(user);
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        filterChain.doFilter(request, response);

                        return;
                    }
                }
                // 그 외 토큰 관련 에러
                throw new CustomException(e.getErrorCode());
            }
        } catch (CustomException e) {
            SecurityContextHolder.clearContext();

            response.setStatus(e.getErrorCode().getHttpStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());

            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }

    private String extractAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private Authentication createAuthentication(User user) {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getId()),  // userId를 principal로 사용
                "",                           // 소셜 로그인이므로 비밀번호 불필요
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,    // credentials
                userDetails.getAuthorities()
        );
    }
}
