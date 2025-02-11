package com.cmc.mercury.global.oauth.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.cmc.mercury.domain.user.entity.OAuthType;
import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.domain.user.entity.UserStatus;
import com.cmc.mercury.domain.user.repository.UserRepository;
import com.cmc.mercury.global.exception.CustomException;
import com.cmc.mercury.global.exception.ErrorCode;
import com.cmc.mercury.global.oauth.apple.AppleIdTokenVerifier;
import com.cmc.mercury.global.oauth.userinfo.AppleOAuthUserInfo;
import com.cmc.mercury.global.oauth.userinfo.GoogleOAuthUserInfo;
import com.cmc.mercury.global.oauth.userinfo.KakaoOAuthUserInfo;
import com.cmc.mercury.global.oauth.userinfo.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final AppleIdTokenVerifier  appleIdTokenVerifier;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        OAuth2User oAuth2User;
        String userNameAttributeName;

        try {
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            log.info("Provider: {}", registrationId);

            if ("apple".equalsIgnoreCase(registrationId)) {
                // Apple 로그인의 경우

                // Apple의 응답에서 id_token 가져오기
                String idToken = userRequest.getAdditionalParameters().get("id_token").toString();
                log.info("Successfully get id_token: {}", idToken);

                // id_token 서명 및 클레임 검증
                DecodedJWT verifiedJwt = appleIdTokenVerifier.verify(idToken);

                // id_token에서 필요한 claim을 추출
                String sub = verifiedJwt.getSubject();
                String email = verifiedJwt.getClaim("email").asString();
                log.info("Apple's verified sub={}, email={}", sub, email);

                // OAuth2User의 attributes 구성
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("sub", sub);
                attributes.put("email", email);

                // SecurityContext에 저장될 DefaultOAuth2User 객체 생성
                oAuth2User = new DefaultOAuth2User(
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                        attributes,
                        "sub"
                );
                userNameAttributeName = "sub";

            } else {
                // Apple 외 다른 OAuth2 로그인
                oAuth2User = super.loadUser(userRequest);
                userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                        .getUserInfoEndpoint().getUserNameAttributeName();
            }

            log.info("OAuth2User attributes: {}", oAuth2User.getAttributes());

            OAuth2UserInfo oAuth2UserInfo
                    = getOAuthUserInfo(userRequest.getClientRegistration().getRegistrationId(), oAuth2User);
            log.info("OAuth2UserInfo 생성 완료: oauthId={}, email={}, type={}",
                    oAuth2UserInfo.getOAuthId(),
                    oAuth2UserInfo.getEmail(),
                    oAuth2UserInfo.getOAuthType());

//        String email = oAuth2UserInfo.getEmail();
            String oauthId = oAuth2UserInfo.getOAuthId();
            OAuthType oAuthType = oAuth2UserInfo.getOAuthType();

            Optional<User> existingUser = userRepository.findByOauthTypeAndOauthId(oAuthType, oauthId);
            boolean isNewUser = existingUser.isEmpty(); // 존재하지 않으면 회원가입

            User user = existingUser.orElseGet(() -> {
                log.info("새로운 사용자 생성 시도");
                return createUser(oAuth2UserInfo);
            });
            log.info("사용자 조회/생성 완료: userId={}", user.getId());

            // 회원가입 여부를 Security Context에 저장 (OAuth2User에 포함)
            Map<String, Object> userAttributes = new HashMap<>(oAuth2User.getAttributes());
            userAttributes.put("isNewUser", isNewUser);

            // 인증 객체 생성 (Security Context에 저장될 인증 정보)
            // 반환된 DefaultOAuth2User는 나중에 @AuthenticationPrincipal로 받아서 필요한 정보를 꺼내 쓸 수 있음
            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                    userAttributes, // 기존 attributes 대신 isNewUser 포함된 attributes
                    userNameAttributeName);

        } catch (Exception e) {
                log.error("OAuth2 로그인 처리 중 오류 발생: ", e);
                throw new CustomException(ErrorCode.OAUTH2_PROCESSING_ERROR);
        }
    }

    private OAuth2UserInfo getOAuthUserInfo(String registrationId, OAuth2User oauth2User) {

        switch (registrationId.toLowerCase()) {
            case "apple":
                return new AppleOAuthUserInfo(oauth2User.getAttributes());
            case "google":
                return new GoogleOAuthUserInfo(oauth2User.getAttributes());
            case "kakao":
                return new KakaoOAuthUserInfo(oauth2User.getAttributes());
            default:
                throw new CustomException(ErrorCode.INVALID_OAUTH2_PROVIDER);
        }
    }

    private User createUser(OAuth2UserInfo oAuth2UserInfo) {

        User user = User.builder()
                .email(oAuth2UserInfo.getEmail())
                .oauthType(oAuth2UserInfo.getOAuthType())
                .oauthId(oAuth2UserInfo.getOAuthId())
                .userStatus(UserStatus.ACTIVE)
                .build();

        return userRepository.save(user);
    }
}
