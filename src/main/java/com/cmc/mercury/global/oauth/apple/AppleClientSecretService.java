package com.cmc.mercury.global.oauth.apple;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cmc.mercury.global.exception.CustomException;
import com.cmc.mercury.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

//import java.io.IOException;
//import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppleClientSecretService {

//    @Value("${apple.key-path}")
//    private String keyPath;
    @Value("${apple.key-content}")
    private String appleKeyContent;

    @Value("${apple.kid}")
    private String keyId;

    @Value("${apple.tid}")
    private String teamId;

    @Value("${apple.cid}")
    private String clientId;

    public String createClientSecret() {
        try {
//             직접 파일 업로드 시 사용
//             log.info("Reading private key from path: {}", keyPath);
//             PrivateKey privateKey = getPrivateKey();

            // Base64 디코딩
            byte[] decoded = Base64.getDecoder().decode(appleKeyContent);
            // PEM 파싱 → PrivateKey
            PrivateKey privateKey = getPrivateKey(new String(decoded, StandardCharsets.UTF_8));
            log.info("Private key successfully created");

            Map<String, Object> headerClaims = new HashMap<>();
            headerClaims.put("kid", keyId);
            headerClaims.put("alg", "ES256");

            Date expirationDate = Date.from(LocalDateTime.now().plusDays(30)
                    .atZone(ZoneId.systemDefault()).toInstant());
            // code/token 교환 직전에 항상 client secret 새로 생성
            String clientSecret = JWT.create()
                    .withHeader(headerClaims)
                    .withKeyId(keyId)
                    .withIssuer(teamId)
                    .withAudience("https://appleid.apple.com")
                    .withSubject(clientId)
                    .withExpiresAt(expirationDate)
                    .withIssuedAt(new Date(System.currentTimeMillis()))
                    .sign(Algorithm.ECDSA256(null, (ECPrivateKey) privateKey));

            log.info("Generated client secret: {}", clientSecret);

            // JWT 디코딩해서 내용 확인
            DecodedJWT jwt = JWT.decode(clientSecret);
            log.info("Decoded JWT - Header: {}", jwt.getHeader());
            log.info("Decoded JWT - Payload: {}", jwt.getPayload());

            return clientSecret;

        } catch (Exception e) {
            throw new CustomException(ErrorCode.APPLE_CLIENT_SECRET_ERROR);
        }
    }

    public PrivateKey getPrivateKey(String keyContent) throws IOException {

        try {
//            직접 파일 업로드 시 사용
//            ClassPathResource resource = new ClassPathResource(keyPath);
//            InputStream in = resource.getInputStream();

//            PEMParser pemParser = new PEMParser(new StringReader(IOUtils.toString(in, StandardCharsets.UTF_8)));
            PEMParser pemParser = new PEMParser(new StringReader(keyContent));
            PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
            log.info("PEMParser created");
            log.info("PEM object read, type: {}", object != null ? object.getClass().getName() : "null");
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

            return converter.getPrivateKey(object);

        } catch (IOException e) {
            throw new CustomException(ErrorCode.APPLE_PRIVATE_KEY_ERROR);
        }
    }
}
