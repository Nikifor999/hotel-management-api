package com.back.hotelshub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hotelsHubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hotels Hub API")
                        .description("""
                                RESTful API for managing hotels, amenities, and related data.
                                Supports search, CRUD, histograms, and more.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Nikifor")
                                .email("dev@hotelshub.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                );
    }
}
