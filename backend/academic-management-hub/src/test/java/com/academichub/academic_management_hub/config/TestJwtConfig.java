package com.academichub.academic_management_hub.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestJwtConfig extends JwtConfig {
    
    @Bean
    @Primary
    public JwtConfig jwtConfig() {
        JwtConfig config = new JwtConfig();
        config.setSecret("verylongandverysecuresecretkeyforhsfivetwelevealgorithmwithenoughbitsverylongandverysecuresecretkeyforhsfivetwelevealgorithmwithenoughbits");
        config.setTokenExpiration(3600000L);
        config.setRefreshTokenExpiration(86400000L);
        return config;
    }
}