package com.cmc.mercury.global.config;

import com.cmc.mercury.domain.user.repository.UserRepository;
import com.cmc.mercury.global.jwt.JwtAuthenticationFilter;
import com.cmc.mercury.global.jwt.JwtProvider;
import com.cmc.mercury.global.oauth.handler.OAuth2FailureHandler;
import com.cmc.mercury.global.oauth.handler.OAuth2SuccessHandler;
import com.cmc.mercury.global.oauth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Swagger UI 관련 경로 허용
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/swagger-resources/**").permitAll()
                        // OAuth2 관련 경로 허용
                        .requestMatchers("/login/**", "/oauth2/**").permitAll()
                         // 도서 검색
                        .requestMatchers("/books/search", "/users/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, userRepository),
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
