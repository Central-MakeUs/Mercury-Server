package com.cmc.mercury.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${swagger.server.https-url}")
    private String httpsUrl;

    @Value("${swagger.server.http-url}")
    private String httpUrl;

    @Bean
    public OpenAPI openAPI() {

        // SecuritySchemes 등록
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .in(SecurityScheme.In.HEADER)
                .name("JwtAuthorization")
                .scheme("Bearer")
                .bearerFormat("JWT");

        SecurityScheme cookieAuth = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("refresh_token");

        return new OpenAPI()
                .servers(List.of(
                        new Server().url(httpsUrl).description("배포(HTTPS)"),
                        new Server().url(httpUrl).description("로컬(HTTP)"),
                        new Server().url("http://localhost:8080").description("localhost")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", bearerAuth)
                        .addSecuritySchemes("cookie-refresh", cookieAuth))
                .security(Arrays.asList(
                        new SecurityRequirement().addList("bearer-jwt"),
                        new SecurityRequirement().addList("cookie-refresh")))
                .info(apiInfo());
    }
    private Info apiInfo() {
        return new Info()
                .title("Mercury API Docs")
                .description("Mercury API 명세서")
                .version("1.0.0");
    }
}
