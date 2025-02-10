package com.cmc.mercury.global.oauth.apple;

import com.cmc.mercury.global.exception.CustomException;
import com.cmc.mercury.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomRequestEntityConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {

    private final AppleClientSecretService appleClientSecretService;
    private final OAuth2AuthorizationCodeGrantRequestEntityConverter defaultConverter =
            new OAuth2AuthorizationCodeGrantRequestEntityConverter();

    @Override
    public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest request) {

        log.info("Converting OAuth2 request for provider: {}", request.getClientRegistration().getRegistrationId());
        RequestEntity<?> entity = defaultConverter.convert(request);
        String registrationId = request.getClientRegistration().getRegistrationId();

        MultiValueMap<String, String> params = (MultiValueMap<String, String>) entity.getBody();

        if ("apple".equals(registrationId)) {
            try {
                // client_secret JWT 생성 및 설정
                String clientSecret = appleClientSecretService.createClientSecret();
                params.set("client_secret", clientSecret);

            } catch (Exception e) {
                throw new CustomException(ErrorCode.APPLE_CLIENT_SECRET_ERROR);
            }
        }

        log.info("Converter의 param: {}", params);

        return new RequestEntity<>(
                params,
                entity.getHeaders(),
                entity.getMethod(),
                entity.getUrl()
        );
    }
}
