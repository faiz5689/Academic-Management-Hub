package com.academichub.academic_management_hub.services.interfaces;

import com.academichub.academic_management_hub.models.RefreshToken;
import com.academichub.academic_management_hub.models.User;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyExpiration(RefreshToken token);
    Optional<RefreshToken> findByToken(String token);
    void revokeTokenByUser(User user);
}