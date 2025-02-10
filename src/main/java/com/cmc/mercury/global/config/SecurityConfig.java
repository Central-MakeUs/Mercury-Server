package com.cmc.mercury.global.config;

import com.cmc.mercury.domain.user.repository.UserRepository;
import com.cmc.mercury.global.jwt.JwtAuthenticationFilter;
import com.cmc.mercury.global.jwt.JwtProvider;
import com.cmc.mercury.global.oauth.apple.AppleClientSecretService;
import com.cmc.mercury.global.oauth.apple.CustomRequestEntityConverter;
import com.cmc.mercury.global.oauth.handler.OAuth2FailureHandler;
import com.cmc.mercury.global.oauth.handler.OAuth2SuccessHandler;
import com.cmc.mercury.global.oauth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final AppleClientSecretService appleClientSecretService;

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {

        DefaultAuthorizationCodeTokenResponseClient client = new DefaultAuthorizationCodeTokenResponseClient();
        client.setRequestEntityConverter(new CustomRequestEntityConverter(appleClientSecretService));

        return client;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // 401, 403에러에 대해서도 CORS 설정 추가
                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                        // Spring Security는 OAuth2 로그인 플로우에서 인증 요청 시 생성된 OAuth2AuthorizationRequest 객체를 HTTP 세션에 저장해두는데,
                        // STATELESS 설정 시 OAuth2AuthorizationRequest가 저장되지 않으므로 콜백 요청 시 이를 찾지 못해 authorization_request_not_found 오류가 발생
                .authorizeHttpRequests(authorize -> authorize
                        // Swagger UI 관련 경로 허용
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/swagger-resources/**").permitAll()
                        // OAuth2 관련 경로 허용
                        .requestMatchers("/login/**", "/oauth2/**").permitAll()
                        // 도서 검색, 사용자 api, health check
                        .requestMatchers("/api/books/search", "/api/users/**", "/api/health").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .tokenEndpoint(token -> token
                                .accessTokenResponseClient(accessTokenResponseClient()))
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, userRepository),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setExposedHeaders(Collections.singletonList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
