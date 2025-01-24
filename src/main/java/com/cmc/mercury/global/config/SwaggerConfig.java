package com.cmc.mercury.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${swagger.server.https-url}")
    private String httpsUrl;

    @Value("${swagger.server.http-url}")
    private String httpUrl;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url(httpsUrl).description("배포(HTTPS)"),
                        new Server().url(httpUrl).description("로컬(HTTP)")
                ))
                .components(new Components())
                .info(apiInfo());
    }
    private Info apiInfo() {
        return new Info()
                .title("Mercury API Docs")
                .description("Mercury API 명세서")
                .version("1.0.0");
    }
}
