package com.academichub.academic_management_hub.services.impl;

import com.academichub.academic_management_hub.exceptions.TokenRefreshException;
import com.academichub.academic_management_hub.models.PasswordResetToken;
import com.academichub.academic_management_hub.models.User;
import com.academichub.academic_management_hub.repositories.PasswordResetTokenRepository;
import com.academichub.academic_management_hub.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceImplTest {
    
    @Mock private PasswordResetTokenRepository tokenRepository;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    
    private PasswordResetServiceImpl passwordResetService;
    private User testUser;
    private PasswordResetToken testToken;
    
    @BeforeEach
    void setUp() {
        passwordResetService = new PasswordResetServiceImpl(tokenRepository, userRepository, passwordEncoder);
        
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("currentHash");
        
        testToken = new PasswordResetToken();
        testToken.setId(UUID.randomUUID());
        testToken.setUser(testUser);
        testToken.setToken(UUID.randomUUID().toString());
        testToken.setExpiryDate(Instant.now().plusSeconds(86400)); // 24 hours
        testToken.setUsed(false);
    }

    @Test
    void createToken_Success() {
        when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(testToken);

        PasswordResetToken result = passwordResetService.createToken(testUser);

        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertFalse(result.isUsed());
        verify(tokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    void createToken_InvalidatesExistingToken() {
        PasswordResetToken existingToken = new PasswordResetToken();
        existingToken.setUser(testUser);
        existingToken.setUsed(false);
        existingToken.setExpiryDate(Instant.now().plusSeconds(3600));

        when(tokenRepository.findByUserAndUsedFalseAndExpiryDateAfter(eq(testUser), any(Instant.class)))
            .thenReturn(Optional.of(existingToken));
        when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(testToken);

        passwordResetService.createToken(testUser);

        assertTrue(existingToken.isUsed());
        verify(tokenRepository, times(2)).save(any(PasswordResetToken.class));
    }

    @Test
    void validateToken_ValidToken_Success() {
        when(tokenRepository.findByToken(testToken.getToken())).thenReturn(Optional.of(testToken));
        
        assertDoesNotThrow(() -> passwordResetService.validateToken(testToken.getToken()));
    }

    @Test
    void validateToken_NonexistentToken_ThrowsException() {
        when(tokenRepository.findByToken(any())).thenReturn(Optional.empty());
        
        assertThrows(TokenRefreshException.class, 
            () -> passwordResetService.validateToken("invalid-token"));
    }

    @Test
    void validateToken_UsedToken_ThrowsException() {
        testToken.setUsed(true);
        when(tokenRepository.findByToken(testToken.getToken())).thenReturn(Optional.of(testToken));
        
        assertThrows(TokenRefreshException.class, 
            () -> passwordResetService.validateToken(testToken.getToken()));
    }

    @Test
    void validateToken_ExpiredToken_ThrowsException() {
        testToken.setExpiryDate(Instant.now().minusSeconds(3600));
        when(tokenRepository.findByToken(testToken.getToken())).thenReturn(Optional.of(testToken));
        
        assertThrows(TokenRefreshException.class, 
            () -> passwordResetService.validateToken(testToken.getToken()));
    }

    @Test
    void resetPassword_Success() {
        String newPassword = "newPassword";
        String newHash = "newHash";
        
        when(tokenRepository.findByToken(testToken.getToken())).thenReturn(Optional.of(testToken));
        when(passwordEncoder.encode(newPassword)).thenReturn(newHash);

        passwordResetService.resetPassword(testToken.getToken(), newPassword);

        assertTrue(testToken.isUsed());
        assertEquals(newHash, testUser.getPasswordHash());
        verify(userRepository).save(testUser);
        verify(tokenRepository).save(testToken);
    }

    @Test
    void resetPassword_InvalidToken_ThrowsException() {
        when(tokenRepository.findByToken(any())).thenReturn(Optional.empty());
        
        assertThrows(TokenRefreshException.class,
            () -> passwordResetService.resetPassword("invalid-token", "newPassword"));
    }

    @Test
    void resetPassword_ExpiredToken_ThrowsException() {
        testToken.setExpiryDate(Instant.now().minusSeconds(3600));
        when(tokenRepository.findByToken(testToken.getToken())).thenReturn(Optional.of(testToken));
        
        assertThrows(TokenRefreshException.class,
            () -> passwordResetService.resetPassword(testToken.getToken(), "newPassword"));
    }

    @Test
    void cleanupExpiredTokens_Success() {
        passwordResetService.cleanupExpiredTokens();
        
        verify(tokenRepository).deleteByExpiryDateBeforeAndUsedFalse(any(Instant.class));
    }
}