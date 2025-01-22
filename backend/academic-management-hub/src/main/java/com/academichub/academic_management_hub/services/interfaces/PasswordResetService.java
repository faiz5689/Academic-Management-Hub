package com.academichub.academic_management_hub.services.interfaces;

import com.academichub.academic_management_hub.models.PasswordResetToken;
import com.academichub.academic_management_hub.models.User;

public interface PasswordResetService {
    PasswordResetToken createToken(User user);
    void validateToken(String token);
    void resetPassword(String token, String newPassword);
    void cleanupExpiredTokens();
}