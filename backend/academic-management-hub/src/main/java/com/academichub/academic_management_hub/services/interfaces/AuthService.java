package com.academichub.academic_management_hub.services.interfaces;

import com.academichub.academic_management_hub.dto.*;

import java.util.UUID;

public interface AuthService {
    AuthResponse authenticate(LoginRequest loginRequest);
    TokenRefreshResponse refreshToken(TokenRefreshRequest request);
    void logout(UUID userId);
    void changePassword(UUID userId, ChangePasswordRequest request);
    void initiatePasswordReset(ResetPasswordRequest request);
    void resetPassword(String token, String newPassword);
    UserDTO registerProfessor(RegistrationRequest request);
    UserDTO getUserById(UUID userId);
}