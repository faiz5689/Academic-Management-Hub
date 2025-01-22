package com.academichub.academic_management_hub.services.impl;

import com.academichub.academic_management_hub.config.JwtConfig;
import com.academichub.academic_management_hub.exceptions.TokenRefreshException;
import com.academichub.academic_management_hub.models.RefreshToken;
import com.academichub.academic_management_hub.models.User;
import com.academichub.academic_management_hub.repositories.RefreshTokenRepository;
import com.academichub.academic_management_hub.services.interfaces.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtConfig jwtConfig;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(jwtConfig.getRefreshTokenExpiration()));
        
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isRevoked()) {
            throw new TokenRefreshException("Refresh token was revoked");
        }
        
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            throw new TokenRefreshException("Refresh token was expired");
        }
        
        return token;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public void revokeTokenByUser(User user) {
        refreshTokenRepository.revokeAllUserTokens(user);
    }
}