package com.cmc.mercury.global.oauth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        log.error("Social Login Failed: {}", exception.getMessage());
        // 예외 메시지를 URI로 인코딩하여 전달
        String errorMessage = URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);

        String targetUrl = UriComponentsBuilder.fromUriString("https://www.mercuryplanet.co.kr/login/fail")
                .queryParam("error", errorMessage)
                .build(true).toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
