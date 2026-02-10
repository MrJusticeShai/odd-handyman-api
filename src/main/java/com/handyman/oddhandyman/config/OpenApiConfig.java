package com.handyman.oddhandyman.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI (Swagger) documentation.
 * <p>
 * Sets up API metadata and global security scheme for JWT authentication.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI specification for the Odd Handyman application.
     * <p>
     * - Sets API title, version, and description.
     * - Configures JWT bearer authentication as a global security requirement.
     *
     * @return the configured {@link OpenAPI} instance
     */
    @Bean
    public OpenAPI oddHandymanApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Odd Handyman API")
                        .version("1.0")
                        .description("API documentation for Odd Handyman App"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ));
    }
}
