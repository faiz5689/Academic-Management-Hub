package com.academichub.academic_management_hub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;  // Add this import

@Configuration
public class BasicAuthConfig {

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails testUser = User.builder()
            .username("test")
            .password(passwordEncoder.encode("test"))
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(testUser);
    }
}