package com.academichub.academic_management_hub.security;

import com.academichub.academic_management_hub.config.JwtConfig;
import com.academichub.academic_management_hub.models.User;
import com.academichub.academic_management_hub.models.UserRole;
import com.academichub.academic_management_hub.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;
    @Mock private UserRepository userRepository;

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private JwtTokenProvider tokenProvider;
    private CustomUserDetailsService userDetailsService;
    private final UUID userId = UUID.randomUUID();
    private User testUser;

    @BeforeEach
    void setUp() {
        JwtConfig jwtConfig = new JwtConfig();
        jwtConfig.setSecret("verylongandverysecuresecretkeyforhsfivetwelevealgorithmwithenoughbitsverylongandverysecuresecretkeyforhsfivetwelevealgorithmwithenoughbits");
        jwtConfig.setTokenExpiration(3600000L);
        
        testUser = createTestUser();
        tokenProvider = new JwtTokenProvider(jwtConfig, userRepository);
        userDetailsService = new CustomUserDetailsService(userRepository);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(tokenProvider, userDetailsService);
        
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldSetAuthentication() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        String token = tokenProvider.generateToken(userId);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldNotSetAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithNoToken_ShouldNotSetAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithException_ShouldContinueChain() throws Exception {
        // Generate a valid token first
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        String token = tokenProvider.generateToken(userId);
        
        // Setup for the actual test
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(userRepository.findById(userId)).thenThrow(new RuntimeException("Test exception"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private User createTestUser() {
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user.setRole(UserRole.STAFF);
        user.setIsActive(true);
        return user;
    }
}