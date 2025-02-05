package com.cmc.mercury.global.oauth.userinfo;

import com.cmc.mercury.domain.user.entity.OAuthType;

import java.util.Map;

public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract OAuthType getOAuthType();

    public abstract String getOAuthId(); // 구글: "sub", 애플: "sub", 카카오: "id"

    public abstract String getEmail();
}
