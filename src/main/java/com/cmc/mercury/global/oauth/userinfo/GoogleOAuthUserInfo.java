package com.cmc.mercury.global.oauth.userinfo;

import com.cmc.mercury.domain.user.entity.OAuthType;

import java.util.Map;

public class GoogleOAuthUserInfo extends OAuth2UserInfo {

    public GoogleOAuthUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public OAuthType getOAuthType() {
        return OAuthType.GOOGLE;
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
