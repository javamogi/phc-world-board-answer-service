package com.phcworld.boardanswerservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "PHC-WORLD ANSWERS Service API 명세서",
                description = "PHC-WORLD RESTful level2 API 명세서 입니다.",
                version = "v1.0.0"))
@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi customTestOpenAPi() {
        String[] paths = {"/answers/**"};

        return GroupedOpenApi
                .builder()
                .group("일반 사용자 ANSWER 도메인에 대한 API")
                .pathsToMatch(paths)
                .build();
    }
}
