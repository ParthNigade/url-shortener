package com.parth.urlshortener.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Distributed URL Shortener API")
                        .version("1.0")
                        .description("A scalable URL shortening service with Redis caching, "
                                + "click analytics, rate limiting, and JWT authentication.")
                        .contact(new Contact()
                                .name("Parth")
                                .email("parth@example.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Auth"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Auth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token")));
    }
}
