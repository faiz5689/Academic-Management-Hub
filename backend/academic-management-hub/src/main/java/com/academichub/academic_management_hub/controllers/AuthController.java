package com.academichub.academic_management_hub.controllers;

import com.academichub.academic_management_hub.dto.*;
import com.academichub.academic_management_hub.security.JwtUserDetails;
import com.academichub.academic_management_hub.services.interfaces.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;



    @PostMapping("/register/professor")
    public ResponseEntity<UserDTO> registerProfessor(@Valid @RequestBody RegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registerProfessor(request));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticate(loginRequest));
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth endpoint accessible");
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal JwtUserDetails userDetails) {
        authService.logout(userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<UserDTO> validateToken(@AuthenticationPrincipal JwtUserDetails userDetails) {
        if (userDetails != null) {
            return ResponseEntity.ok(authService.getUserById(userDetails.getId()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    @PostMapping("/password/change")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(userDetails.getId(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset/request")
    public ResponseEntity<String> requestPasswordReset(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            authService.initiatePasswordReset(request);
            return ResponseEntity.ok("Password reset email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error sending password reset email: " + e.getMessage());
        }
    }

    @PostMapping("/password/reset")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        try {
            authService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password reset successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error resetting password: " + e.getMessage());
        }
    }
}