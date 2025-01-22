// package com.academichub.academic_management_hub.controllers;

// import com.academichub.academic_management_hub.dto.*;
// import com.academichub.academic_management_hub.security.JwtUserDetails;
// import com.academichub.academic_management_hub.services.interfaces.AuthService;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/api/auth")
// @RequiredArgsConstructor
// public class AuthController {
//     private final AuthService authService;

//     @PostMapping("/login")
//     public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
//         return ResponseEntity.ok(authService.authenticate(loginRequest));
//     }

//     @PostMapping("/refresh")
//     public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
//         return ResponseEntity.ok(authService.refreshToken(request));
//     }

//     @PostMapping("/logout")
//     public ResponseEntity<Void> logout(@AuthenticationPrincipal JwtUserDetails userDetails) {
//         authService.logout(userDetails.getId());
//         return ResponseEntity.ok().build();
//     }

//     @PostMapping("/password/change")
//     public ResponseEntity<Void> changePassword(
//             @AuthenticationPrincipal JwtUserDetails userDetails,
//             @Valid @RequestBody ChangePasswordRequest request) {
//         authService.changePassword(userDetails.getId(), request);
//         return ResponseEntity.ok().build();
//     }

//     @PostMapping("/password/reset/request")
//     public ResponseEntity<Void> requestPasswordReset(@Valid @RequestBody ResetPasswordRequest request) {
//         authService.initiatePasswordReset(request);
//         return ResponseEntity.ok().build();
//     }

//     @PostMapping("/password/reset")
//     public ResponseEntity<Void> resetPassword(
//             @RequestParam String token,
//             @RequestParam String newPassword) {
//         authService.resetPassword(token, newPassword);
//         return ResponseEntity.ok().build();
//     }
// }