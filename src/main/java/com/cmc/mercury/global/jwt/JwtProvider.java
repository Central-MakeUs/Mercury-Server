package com.cmc.mercury.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.domain.user.repository.UserRepository;
import com.cmc.mercury.global.exception.CustomException;
import com.cmc.mercury.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    public String createAccessToken(Long userId, String email) {

        return createToken(userId, email, "AccessToken", accessTokenValidity);
    }

    public String createRefreshToken(Long userId, String email) {

        String refreshToken = createToken(userId, email, "RefreshToken", refreshTokenValidity);
        updateRefreshToken(userId, refreshToken);
        return refreshToken;
    }

    public String createToken(Long userId, String email, String tokenType, long validityInMilliseconds) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + validityInMilliseconds);

        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("email", email)
                .withClaim("type", tokenType)
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    private void updateRefreshToken(Long userId, String refreshToken) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.updateRefreshToken(refreshToken);
        userRepository.save(user);
    }

    public void validateToken(String token, String expectedType) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("type", expectedType) // ACCESS인지 REFRESH인지 검증
                    .build();

            // 서명이 유효한지, 만료되지 않았는지 검증
            verifier.verify(token);

        } catch (TokenExpiredException e) {
            // 토큰이 만료된 경우: Access Token과 Refresh Token을 구분하여 만료 예외 던지기
            if ("AccessToken".equals(expectedType)) {
                throw new CustomException(ErrorCode.EXPIRED_ACCESS_TOKEN);

            } else {
                throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
            }

        } catch (SignatureVerificationException | JWTDecodeException e) {
            // 서명이 유효하지 않거나 토큰 형식이 잘못된 경우
            throw new CustomException(ErrorCode.INVALID_TOKEN);

        } catch (InvalidClaimException e) {
            // 토큰 타입이 일치하지 않는 경우
            throw new CustomException(ErrorCode.TOKEN_TYPE_MISMATCH);

        } catch (JWTVerificationException e) {
            // 그 외 모든 JWT 검증 실패
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    public User getUserFromToken(String token) {

        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);

        Long userId = Long.parseLong(jwt.getSubject()); // subject에서 User 엔티티의 id 추출

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return user;
    }

    public void checkRefreshToken(String token) {

        // 기존 Refresh Token이 DB에 저장된 것과 일치하는지 확인
        User user = getUserFromToken(token);

        String storedRefreshToken = user.getRefreshToken();
           if (storedRefreshToken == null) {
            // 사용자가 로그아웃한 경우
            throw new CustomException(ErrorCode.EMPTY_REFRESH_TOKEN);
        }

/*        if (!token.equals(user.getRefreshToken())) {
            // DB의 Refresh Token과 일치하지 않으면 재사용 시도로 간주
            user.updateRefreshToken(null);  // DB의 Refresh Token 무효화
            userRepository.save(user);
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }*/
        if (!token.equals(storedRefreshToken)) {
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }
    }

    public String refreshToken(String refreshToken) {

        // Refresh Token이 없음
        if (!StringUtils.hasText(refreshToken)) {
            throw new CustomException(ErrorCode.EMPTY_REFRESH_TOKEN);
        }

        // Refresh Token 검증
        validateToken(refreshToken, "RefreshToken");
        // DB에 저장된 Refresh Token과 비교하여 유효성 확인
        checkRefreshToken(refreshToken);

        // Refresh Token이 유효하면 새로운 Refresh Token 발급
        User user = getUserFromToken(refreshToken);
        String newRefreshToken = createRefreshToken(user.getId(), user.getEmail());
        // 새 Refresh Token을 DB에 저장
        updateRefreshToken(user.getId(), newRefreshToken);

        return newRefreshToken;
    }
}
