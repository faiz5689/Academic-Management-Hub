package com.academichub.academic_management_hub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@ConfigurationProperties(prefix = "jwt")
@Configuration
@Data
public class JwtConfig {
    private String secret;
    private long tokenExpiration = 3600000; // 1 hour
    private long refreshTokenExpiration = 604800000; // 7 days
    private String issuer = "Academic Hub";
}