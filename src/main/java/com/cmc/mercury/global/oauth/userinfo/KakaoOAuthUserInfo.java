package com.cmc.mercury.global.oauth.userinfo;

import com.cmc.mercury.domain.user.entity.OAuthType;

import java.util.Map;

public class KakaoOAuthUserInfo extends OAuth2UserInfo {

    public KakaoOAuthUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public OAuthType getOAuthType() {
        return OAuthType.KAKAO;
    }

    @Override
    public String getOAuthId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        return (String) account.get("email");
    }
}
