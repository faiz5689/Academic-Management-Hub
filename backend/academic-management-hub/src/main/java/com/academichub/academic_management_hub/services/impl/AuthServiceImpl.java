// package com.academichub.academic_management_hub.services.impl;

// import com.academichub.academic_management_hub.dto.*;
// import com.academichub.academic_management_hub.exceptions.TokenRefreshException;
// import com.academichub.academic_management_hub.models.PasswordResetToken;
// import com.academichub.academic_management_hub.models.RefreshToken;
// import com.academichub.academic_management_hub.models.User;
// import com.academichub.academic_management_hub.repositories.UserRepository;
// import com.academichub.academic_management_hub.security.JwtTokenProvider;
// import com.academichub.academic_management_hub.services.interfaces.AuthService;
// import com.academichub.academic_management_hub.services.interfaces.RefreshTokenService;
// import jakarta.persistence.EntityNotFoundException;
// import lombok.RequiredArgsConstructor;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.Instant;
// import java.time.ZonedDateTime;
// import java.util.UUID;

// @Service
// @RequiredArgsConstructor
// public class AuthServiceImpl implements AuthService {
//     private final AuthenticationManager authenticationManager;
//     private final JwtTokenProvider tokenProvider;
//     private final RefreshTokenService refreshTokenService;
//     private final UserRepository userRepository;
//     private final PasswordEncoder passwordEncoder;

//     @Override
//     @Transactional
//     public AuthResponse authenticate(LoginRequest loginRequest) {
//         Authentication authentication = authenticationManager.authenticate(
//             new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
//         );

//         User user = userRepository.findByEmail(loginRequest.getEmail())
//             .orElseThrow(() -> new EntityNotFoundException("User not found"));
//         user.setLastLogin(ZonedDateTime.now());
//         userRepository.save(user);

//         String accessToken = tokenProvider.generateToken(authentication);
//         RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

//         return AuthResponse.builder()
//             .accessToken(accessToken)
//             .refreshToken(refreshToken.getToken())
//             .build();
//     }

//     @Override
//     @Transactional
//     public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
//         RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken())
//             .orElseThrow(() -> new TokenRefreshException("Refresh token not found"));

//         refreshTokenService.verifyExpiration(refreshToken);

//         String accessToken = tokenProvider.generateToken(refreshToken.getUser().getId());

//         return TokenRefreshResponse.builder()
//             .accessToken(accessToken)
//             .refreshToken(request.getRefreshToken())
//             .build();
//     }

//     @Override
//     @Transactional
//     public void logout(UUID userId) {
//         User user = userRepository.findById(userId)
//             .orElseThrow(() -> new EntityNotFoundException("User not found"));
//         refreshTokenService.revokeTokenByUser(user);
//     }

//     @Override
//     @Transactional
//     public void changePassword(UUID userId, ChangePasswordRequest request) {
//         User user = userRepository.findById(userId)
//             .orElseThrow(() -> new EntityNotFoundException("User not found"));

//         if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
//             throw new IllegalArgumentException("Current password is incorrect");
//         }

//         user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
//         userRepository.save(user);
//         refreshTokenService.revokeTokenByUser(user);
//     }

//     @Override
//     public void initiatePasswordReset(ResetPasswordRequest request) {
//         // Implementation depends on email service configuration
//         throw new UnsupportedOperationException("Password reset not implemented");
//     }

//     @Override
//     public void resetPassword(String token, String newPassword) {
//         // Implementation depends on email service configuration
//         throw new UnsupportedOperationException("Password reset not implemented");
//     }
// }