package com.flipkart.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import org.springframework.context.annotation.*;

@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Flipkart Clone E-Commerce API")
                        .version("1.0.0")
                        .description("Complete e-commerce REST API with JWT authentication, " +
                                "product management, cart, orders, reviews, wishlist, and coupons.")
                        .contact(new Contact()
                                .name("FlipkartClone")
                                .email("support@flipkartclone.com"))
                        .license(new License().name("MIT")));
    }
}
