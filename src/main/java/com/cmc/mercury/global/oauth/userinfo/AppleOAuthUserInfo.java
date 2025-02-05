package com.cmc.mercury.global.oauth.userinfo;

import com.cmc.mercury.domain.user.entity.OAuthType;

import java.util.Map;

public class AppleOAuthUserInfo extends OAuth2UserInfo {

    public AppleOAuthUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public OAuthType getOAuthType() {
        return OAuthType.APPLE;
    }

    @Override
    public String getOAuthId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}
