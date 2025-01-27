package com.academichub.academic_management_hub.services.impl;

import com.academichub.academic_management_hub.dto.*;
import com.academichub.academic_management_hub.exceptions.EmailAlreadyExistsException;
import com.academichub.academic_management_hub.exceptions.ResourceAlreadyExistsException;
import com.academichub.academic_management_hub.exceptions.TokenRefreshException;
import com.academichub.academic_management_hub.models.Department;
import com.academichub.academic_management_hub.models.Professor;
import com.academichub.academic_management_hub.models.RefreshToken;
import com.academichub.academic_management_hub.models.User;
import com.academichub.academic_management_hub.models.UserRole;
import com.academichub.academic_management_hub.repositories.DepartmentRepository;
import com.academichub.academic_management_hub.repositories.ProfessorRepository;
import com.academichub.academic_management_hub.repositories.UserRepository;
import com.academichub.academic_management_hub.security.JwtTokenProvider;
import com.academichub.academic_management_hub.services.interfaces.AuthService;
import com.academichub.academic_management_hub.services.interfaces.EmailService;
import com.academichub.academic_management_hub.services.interfaces.RefreshTokenService;
import com.academichub.academic_management_hub.services.interfaces.PasswordResetService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetService passwordResetService;
    private final UserRepository userRepository;
    private final ProfessorRepository professorRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional
    public AuthResponse authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
            
        user.setLastLogin(ZonedDateTime.now());
        userRepository.save(user);

        String accessToken = tokenProvider.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken.getToken())
            .build();
    }

    @Override
    @Transactional
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken())
            .orElseThrow(() -> new TokenRefreshException("Refresh token not found"));

        refreshTokenService.verifyExpiration(refreshToken);

        String accessToken = tokenProvider.generateToken(refreshToken.getUser().getId());

        return TokenRefreshResponse.builder()
            .accessToken(accessToken)
            .refreshToken(request.getRefreshToken())
            .tokenType("Bearer")
            .build();
    }

    @Override
    @Transactional
    public void logout(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
            
        String token = getCurrentToken();
        if (token != null) {
            tokenProvider.revokeToken(token, user);
        }
        refreshTokenService.revokeTokenByUser(user);
    }

    private String getCurrentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() instanceof String) {
            return (String) authentication.getCredentials();
        }
        return null;
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        // Revoke all refresh tokens when password is changed
        refreshTokenService.revokeTokenByUser(user);
    }

    @Override
    @Transactional
    public void initiatePasswordReset(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
            
        var resetToken = passwordResetService.createToken(user);
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken.getToken());
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        passwordResetService.validateToken(token);
        passwordResetService.resetPassword(token, newPassword);
    }

    @Override
    @Transactional
    public UserDTO registerProfessor(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new EntityNotFoundException("Department not found"));

        User user = User.builder()
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .role(UserRole.PROFESSOR)
            .isActive(true)
            .build();
        
        user = userRepository.save(user);

        Professor professor = Professor.builder()
            .user(user)
            .department(department)
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .title(request.getTitle())
            .officeLocation(request.getOfficeLocation())
            .phone(request.getPhone())
            .build();

        if (request.getResearchInterests() != null) {
            professor.setResearchInterestsList(request.getResearchInterests());
        }

        professorRepository.save(professor);
        
        return UserDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .role(user.getRole())
            .isActive(user.getIsActive())
            .createdAt(user.getCreatedAt())
            .lastLogin(user.getLastLogin())
            .build();
    }

}