package com.academichub.academic_management_hub.config;

import com.academichub.academic_management_hub.security.*;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

import com.academichub.academic_management_hub.models.User;
import com.academichub.academic_management_hub.models.UserRole;
import com.academichub.academic_management_hub.repositories.RevocatedTokenRepository;
import com.academichub.academic_management_hub.repositories.UserRepository;

import java.util.Base64;
import java.util.Optional;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class TestSecurityConfig {

    @Bean
    @Primary
    public UserRepository userRepository() {
        UserRepository repository = Mockito.mock(UserRepository.class);
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("$2a$10$ZvXOu9xLaWRzXAaB3u0P4.kfUKGF7yyu0/ARULBGrVxVQYVXi3gEq");
        testUser.setRole(UserRole.STAFF);
        testUser.setIsActive(true);
        
        Mockito.when(repository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(testUser));
        
        return repository;
    }

    @Bean
    @Primary
    public CustomUserDetailsService customUserDetailsService(UserRepository userRepository) {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    @Primary
    public JwtConfig jwtConfig() {
        JwtConfig config = new JwtConfig();
        config.setSecret("verylongandverysecuresecretkeyforhsfivetwelevealgorithmwithenoughbitsverylongandverysecuresecretkeyforhsfivetwelevealgorithmwithenoughbits");
        config.setTokenExpiration(3600000);
        config.setRefreshTokenExpiration(604800000);
        config.setIssuer("Academic Hub Test");
        return config;
    }

    @Bean
    @Primary
    public JwtTokenProvider jwtTokenProvider(JwtConfig jwtConfig, UserRepository userRepository, RevocatedTokenRepository revocatedTokenRepository) {
    return new JwtTokenProvider(jwtConfig, userRepository, revocatedTokenRepository);
    }

    @Bean
    @Primary
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    @Primary
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http,
            JwtTokenProvider tokenProvider,
            CustomUserDetailsService userDetailsService) throws Exception {
        
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll())
            .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, userDetailsService), 
                UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}