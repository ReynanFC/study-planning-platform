package com.study.study_planning_platform.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Project Hackathon - study-planning-platform")
                        .version("v1")
                        .description("")
                        .termsOfService("https://github.com/ReynanFC/study-planning-platform")
                        );
    }
}
