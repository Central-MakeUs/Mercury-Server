package com.cmc.mercury.global.oauth.apple;


import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cmc.mercury.global.exception.CustomException;
import com.cmc.mercury.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;

@Service
@Slf4j
public class AppleIdTokenVerifier {

    @Value("${apple.cid}")
    private String clientId;

    public DecodedJWT verify(String idToken) {
        try {

            // id_token 헤더 파싱 (kid, alg 등 확인)
            DecodedJWT unverifiedJWT = JWT.decode(idToken);
            String keyId = unverifiedJWT.getKeyId();
            String algFromToken = unverifiedJWT.getAlgorithm();
            log.info("Apple id_token의 kid = {}, alg = {}", keyId, algFromToken);

            // 애플의 공개키 엔드포인트 URL
            URL appleJwkUrl = new URL("https://appleid.apple.com/auth/keys");
            JwkProvider jwkProvider = new UrlJwkProvider(appleJwkUrl);
            Jwk jwk = jwkProvider.get(keyId);
            PublicKey publicKey = jwk.getPublicKey();
            log.info("애플 공개키 성공적으로 가져옴, key type: {}", publicKey.getAlgorithm());

            // 공개키가 RSA인지 EC인지 구분하여 Algorithm 인스턴스 생성
            Algorithm algorithm;
            if ("EC".equalsIgnoreCase(publicKey.getAlgorithm())) {
                // EC 키인 경우
                algorithm = Algorithm.ECDSA256((ECPublicKey) publicKey, null);
            } else if ("RSA".equalsIgnoreCase(publicKey.getAlgorithm())) {
                // RSA 키인 경우
                algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, null);
            } else {
                throw new IllegalArgumentException("지원하지 않는 공개키 타입: " + publicKey.getAlgorithm());
            }

            // JWTVerifier를 생성하여 서명, issuer, audience 등의 클레임 검증
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("https://appleid.apple.com")
                    .withAudience(clientId)
                    .build();

            // 서명 및 필수 클레임 검증
            DecodedJWT verifiedJwt = verifier.verify(idToken);
            log.info("id_token 검증 성공: subject={}, issuer={}", verifiedJwt.getSubject(), verifiedJwt.getIssuer());

            return verifiedJwt;

        } catch (Exception e) {
            log.error("id_token 검증 실패", e);
            throw new CustomException(ErrorCode.APPLE_TOKEN_VALIDATION_ERROR);
        }
    }
}
