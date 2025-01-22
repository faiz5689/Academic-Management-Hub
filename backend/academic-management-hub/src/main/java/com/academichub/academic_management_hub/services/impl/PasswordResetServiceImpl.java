package com.academichub.academic_management_hub.services.impl;

import com.academichub.academic_management_hub.exceptions.TokenRefreshException;
import com.academichub.academic_management_hub.models.PasswordResetToken;
import com.academichub.academic_management_hub.models.User;
import com.academichub.academic_management_hub.repositories.PasswordResetTokenRepository;
import com.academichub.academic_management_hub.repositories.UserRepository;
import com.academichub.academic_management_hub.services.interfaces.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {
    private static final Duration TOKEN_VALIDITY = Duration.ofHours(24);
    
    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public PasswordResetToken createToken(User user) {
        tokenRepository.findByUserAndUsedFalseAndExpiryDateAfter(user, Instant.now())
            .ifPresent(token -> {
                token.setUsed(true);
                tokenRepository.save(token);
            });

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plus(TOKEN_VALIDITY));
        return tokenRepository.save(token);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
            .orElseThrow(() -> new TokenRefreshException("Invalid password reset token"));

        if (resetToken.isUsed()) {
            throw new TokenRefreshException("Token has already been used");
        }

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new TokenRefreshException("Token has expired");
        }
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
            .orElseThrow(() -> new TokenRefreshException("Invalid password reset token"));

        if (resetToken.isUsed() || resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new TokenRefreshException("Invalid or expired token");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }

    @Override
    @Scheduled(cron = "0 0 */6 * * *") // Run every 6 hours
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiryDateBeforeAndUsedFalse(Instant.now());
    }
}