package com.academichub.academic_management_hub.security;

import com.academichub.academic_management_hub.config.JwtConfig;
import com.academichub.academic_management_hub.models.User;
import com.academichub.academic_management_hub.models.UserRole;
import com.academichub.academic_management_hub.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    private UserRepository userRepository;

    private JwtConfig jwtConfig;
    private JwtTokenProvider tokenProvider;
    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        jwtConfig = new JwtConfig();
        jwtConfig.setSecret("verylongandverysecuresecretkeyforhsfivetwelevealgorithmwithenoughbitsverylongandverysecuresecretkeyforhsfivetwelevealgorithmwithenoughbits");
        jwtConfig.setTokenExpiration(3600000L);
        jwtConfig.setRefreshTokenExpiration(604800000L);
        jwtConfig.setIssuer("Test Issuer");

        tokenProvider = new JwtTokenProvider(jwtConfig, userRepository);
        userId = UUID.randomUUID();
        testUser = createTestUser();
    }

    @Test
    void generateToken_WithValidAuthentication_ShouldReturnValidToken() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        JwtUserDetails userDetails = new JwtUserDetails(testUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);

        String token = tokenProvider.generateToken(auth);

        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals(userId, tokenProvider.getUserIdFromToken(token));
    }

    @Test
    void generateToken_WithUserId_ShouldReturnValidToken() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        String token = tokenProvider.generateToken(userId);

        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals(userId, tokenProvider.getUserIdFromToken(token));
    }

    @Test
    void generateRefreshToken_ShouldReturnValidToken() {
        String refreshToken = tokenProvider.generateRefreshToken(userId);

        assertNotNull(refreshToken);
        assertTrue(tokenProvider.validateToken(refreshToken));
        assertEquals(userId, tokenProvider.getUserIdFromToken(refreshToken));
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() {
        jwtConfig.setTokenExpiration(0L);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        
        String token = tokenProvider.generateToken(userId);
        assertFalse(tokenProvider.validateToken(token));
    }

    @Test
    void validateToken_WithInvalidSignature_ShouldReturnFalse() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        String token = tokenProvider.generateToken(userId);
        String tamperedToken = token.substring(0, token.length() - 4) + "invalid";
        
        assertFalse(tokenProvider.validateToken(tamperedToken));
    }

    @Test
    void getUserIdFromToken_WithValidToken_ShouldReturnCorrectUserId() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        String token = tokenProvider.generateToken(userId);
        
        UUID extractedUserId = tokenProvider.getUserIdFromToken(token);
        assertEquals(userId, extractedUserId);
    }

    @Test
    void generateToken_WithNonExistentUser_ShouldThrowEntityNotFoundException() {
        UUID nonExistentUserId = UUID.randomUUID();
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, 
            () -> tokenProvider.generateToken(nonExistentUserId));
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