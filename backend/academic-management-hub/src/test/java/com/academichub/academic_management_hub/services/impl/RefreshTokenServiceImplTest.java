package com.academichub.academic_management_hub.services.impl;

import com.academichub.academic_management_hub.config.JwtConfig;
import com.academichub.academic_management_hub.exceptions.TokenRefreshException;
import com.academichub.academic_management_hub.models.RefreshToken;
import com.academichub.academic_management_hub.models.User;
import com.academichub.academic_management_hub.repositories.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private JwtConfig jwtConfig;
    private RefreshTokenServiceImpl refreshTokenService;

    private User testUser;
    private RefreshToken testToken;

    @BeforeEach
    void setUp() {
        jwtConfig = new JwtConfig();
        jwtConfig.setSecret("verylongandverysecuresecretkeyforhsfivetwelevealgorithmwithenoughbitsverylongandverysecuresecretkeyforhsfivetwelevealgorithmwithenoughbits");
        jwtConfig.setTokenExpiration(3600000L);
        jwtConfig.setRefreshTokenExpiration(3600000L);
        
        refreshTokenService = new RefreshTokenServiceImpl(refreshTokenRepository, jwtConfig);
        
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        
        testToken = new RefreshToken();
        testToken.setId(UUID.randomUUID());
        testToken.setUser(testUser);
        testToken.setToken(UUID.randomUUID().toString());
        testToken.setExpiryDate(Instant.now().plusMillis(3600000));
        testToken.setRevoked(false);
    }

    @Test
    void createRefreshToken_Success() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testToken);

        RefreshToken result = refreshTokenService.createRefreshToken(testUser);

        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertFalse(result.isRevoked());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void verifyExpiration_ValidToken_Success() {
        RefreshToken result = refreshTokenService.verifyExpiration(testToken);

        assertNotNull(result);
        assertEquals(testToken, result);
    }

    @Test
    void verifyExpiration_ExpiredToken_ThrowsException() {
        testToken.setExpiryDate(Instant.now().minusSeconds(3600));

        assertThrows(TokenRefreshException.class,
            () -> refreshTokenService.verifyExpiration(testToken),
            "Refresh token was expired"
        );
    }

    @Test
    void verifyExpiration_RevokedToken_ThrowsException() {
        testToken.setRevoked(true);

        assertThrows(TokenRefreshException.class,
            () -> refreshTokenService.verifyExpiration(testToken),
            "Refresh token was revoked"
        );
    }

    @Test
    void findByToken_ExistingToken_ReturnsToken() {
        when(refreshTokenRepository.findByToken(testToken.getToken()))
            .thenReturn(Optional.of(testToken));

        Optional<RefreshToken> result = refreshTokenService.findByToken(testToken.getToken());

        assertTrue(result.isPresent());
        assertEquals(testToken, result.get());
        verify(refreshTokenRepository).findByToken(testToken.getToken());
    }

    @Test
    void findByToken_NonExistentToken_ReturnsEmpty() {
        String nonExistentToken = UUID.randomUUID().toString();
        when(refreshTokenRepository.findByToken(nonExistentToken))
            .thenReturn(Optional.empty());

        Optional<RefreshToken> result = refreshTokenService.findByToken(nonExistentToken);

        assertTrue(result.isEmpty());
        verify(refreshTokenRepository).findByToken(nonExistentToken);
    }

    @Test
    void revokeTokenByUser_Success() {
        refreshTokenService.revokeTokenByUser(testUser);

        verify(refreshTokenRepository).revokeAllUserTokens(testUser);
    }
}