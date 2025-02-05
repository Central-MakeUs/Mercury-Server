package com.cmc.mercury.global.oauth.service;

import com.cmc.mercury.domain.user.entity.OAuthType;
import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.domain.user.entity.UserStatus;
import com.cmc.mercury.domain.user.repository.UserRepository;
import com.cmc.mercury.global.exception.CustomException;
import com.cmc.mercury.global.exception.ErrorCode;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

            log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

            OAuth2User oAuth2User = super.loadUser(userRequest);
            log.info("OAuth2User attributes: {}", oAuth2User.getAttributes());

        try {
            OAuth2UserInfo oAuth2UserInfo
                    = getOAuthUserInfo(userRequest.getClientRegistration().getRegistrationId(), oAuth2User);
            log.info("OAuth2UserInfo 생성 완료: oauthId={}, email={}, type={}",
                    oAuth2UserInfo.getOAuthId(),
                    oAuth2UserInfo.getEmail(),
                    oAuth2UserInfo.getOAuthType());

//        String email = oAuth2UserInfo.getEmail();
            String oauthId = oAuth2UserInfo.getOAuthId();
            OAuthType oAuthType = oAuth2UserInfo.getOAuthType();

            User user = userRepository.findByOauthTypeAndOauthId(oAuthType, oauthId)
                    .orElseGet(() -> {
                        log.info("새로운 사용자 생성 시도");
                        return createUser(oAuth2UserInfo);
                    });
            log.info("사용자 조회/생성 완료: userId={}", user.getId());

            // 인증 객체 생성 (Security Context에 저장될 인증 정보)
            // 반환된 DefaultOAuth2User는 나중에 @AuthenticationPrincipal로 받아서 필요한 정보를 꺼내 쓸 수 있음
            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                    oAuth2User.getAttributes(),
                    userRequest.getClientRegistration().getProviderDetails()
                            .getUserInfoEndpoint().getUserNameAttributeName()
            );
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
