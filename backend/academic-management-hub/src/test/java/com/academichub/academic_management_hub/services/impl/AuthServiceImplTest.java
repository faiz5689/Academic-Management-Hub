package com.academichub.academic_management_hub.services.impl;

import com.academichub.academic_management_hub.config.JwtConfig;
import com.academichub.academic_management_hub.dto.*;
import com.academichub.academic_management_hub.exceptions.TokenRefreshException;
import com.academichub.academic_management_hub.models.*;
import com.academichub.academic_management_hub.repositories.UserRepository;
import com.academichub.academic_management_hub.security.JwtTokenProvider;
import com.academichub.academic_management_hub.security.JwtUserDetails;
import com.academichub.academic_management_hub.services.interfaces.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private PasswordResetService passwordResetService;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;

    private JwtTokenProvider tokenProvider;
    private AuthServiceImpl authService;
    private User testUser;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        
        JwtConfig jwtConfig = new JwtConfig();
        jwtConfig.setSecret("verylongandverysecuresecretkeyforhsfivetwelevealgorithmwithenoughbitsverylongandverysecuresecretkeyforhsfivetwelevealgorithmwithenoughbits");
        jwtConfig.setTokenExpiration(3600000L);
        
        tokenProvider = new JwtTokenProvider(jwtConfig, userRepository);
        testRefreshToken = createTestRefreshToken();
        
        authService = new AuthServiceImpl(
            authenticationManager, 
            tokenProvider,
            refreshTokenService,
            passwordResetService,
            userRepository,
            passwordEncoder,
            emailService
        );
    }

    @Test
    void authenticate_ValidCredentials_ReturnsAuthResponse() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        
        JwtUserDetails userDetails = new JwtUserDetails(testUser);
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        
        // For AuthService
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(refreshTokenService.createRefreshToken(testUser)).thenReturn(testRefreshToken);
        
        // For JwtTokenProvider
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        AuthResponse response = authService.authenticate(request);

        assertNotNull(response);
        assertTrue(response.getAccessToken().length() > 0);
        assertEquals(testRefreshToken.getToken(), response.getRefreshToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void authenticate_InvalidCredentials_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");
        
        when(authenticationManager.authenticate(any()))
            .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(request));
    }

    @Test
    void refreshToken_ValidToken_ReturnsNewTokens() {
        TokenRefreshRequest request = new TokenRefreshRequest();
        request.setRefreshToken(testRefreshToken.getToken());
        
        when(refreshTokenService.findByToken(request.getRefreshToken()))
            .thenReturn(Optional.of(testRefreshToken));
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        TokenRefreshResponse response = authService.refreshToken(request);

        assertNotNull(response);
        assertTrue(response.getAccessToken().length() > 0);
        assertEquals(request.getRefreshToken(), response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
    }

    @Test
    void refreshToken_InvalidToken_ThrowsException() {
        TokenRefreshRequest request = new TokenRefreshRequest();
        request.setRefreshToken("invalid-token");
        
        when(refreshTokenService.findByToken(request.getRefreshToken()))
            .thenReturn(Optional.empty());

        assertThrows(TokenRefreshException.class, () -> authService.refreshToken(request));
    }

    @Test
    void changePassword_ValidRequest_UpdatesPassword() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newPassword");
        
        when(passwordEncoder.matches(request.getCurrentPassword(), testUser.getPasswordHash()))
            .thenReturn(true);
        when(passwordEncoder.encode(request.getNewPassword()))
            .thenReturn("newPasswordHash");

        authService.changePassword(testUser.getId(), request);

        verify(userRepository).save(testUser);
        verify(refreshTokenService).revokeTokenByUser(testUser);
        assertEquals("newPasswordHash", testUser.getPasswordHash());
    }

    @Test
    void initiatePasswordReset_ValidEmail_SendsResetEmail() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("test@example.com");
        
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        PasswordResetToken resetToken = createTestPasswordResetToken();
        when(passwordResetService.createToken(testUser))
            .thenReturn(resetToken);

        authService.initiatePasswordReset(request);

        verify(emailService).sendPasswordResetEmail(eq(testUser.getEmail()), eq(resetToken.getToken()));
    }

    @Test
    void logout_ValidUserId_RevokesTokens() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        authService.logout(testUser.getId());
        verify(refreshTokenService).revokeTokenByUser(testUser);
    }

    @Test
    void resetPassword_ValidToken_UpdatesPassword() {
        String token = "valid-token";
        String newPassword = "newPassword";
        
        authService.resetPassword(token, newPassword);
        
        verify(passwordResetService).validateToken(token);
        verify(passwordResetService).resetPassword(token, newPassword);
    }

    private User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user.setRole(UserRole.STAFF);
        user.setIsActive(true);
        return user;
    }

    private RefreshToken createTestRefreshToken() {
        RefreshToken token = new RefreshToken();
        token.setUser(testUser);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusSeconds(3600));
        return token;
    }

    private PasswordResetToken createTestPasswordResetToken() {
        PasswordResetToken token = new PasswordResetToken();
        token.setId(UUID.randomUUID());
        token.setUser(testUser);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusSeconds(3600));
        return token;
    }
}