package com.academichub.academic_management_hub.security;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.academichub.academic_management_hub.config.JwtConfig;
import com.academichub.academic_management_hub.models.User;
import com.academichub.academic_management_hub.models.RevocatedToken;
import com.academichub.academic_management_hub.repositories.UserRepository;
import com.academichub.academic_management_hub.repositories.RevocatedTokenRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.Date;
import java.util.UUID;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;
    private final RevocatedTokenRepository revocatedTokenRepository;

    public String generateToken(Authentication authentication) {
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        return generateToken(userDetails.getId());
    }

    public String generateToken(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getTokenExpiration());

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .setIssuer(jwtConfig.getIssuer())
                .claim("email", user.getEmail())
                .claim("roles", Collections.singletonList("ROLE_" + user.getRole().name()))
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret())
                .compact();
    }

    public String generateRefreshToken(UUID userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getRefreshTokenExpiration());

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .setIssuer(jwtConfig.getIssuer())
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret())
                .compact();
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtConfig.getSecret())
                .parseClaimsJws(token)
                .getBody();

        return UUID.fromString(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            if (revocatedTokenRepository.existsByToken(token)) {
                return false;
            }
            Jwts.parser().setSigningKey(jwtConfig.getSecret()).parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            System.out.println("Token validation failed: " + ex.getMessage());
            return false;
        }
    }

    public void revokeToken(String token, User user) {
        try {
            Claims claims = Jwts.parser()
                .setSigningKey(jwtConfig.getSecret())
                .parseClaimsJws(token)
                .getBody();
                
            RevocatedToken revocatedToken = new RevocatedToken();
            revocatedToken.setToken(token);
            revocatedToken.setUser(user);
            revocatedToken.setExpiryDate(new Date(claims.getExpiration().getTime()).toInstant());
            revocatedTokenRepository.save(revocatedToken);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid token");
        }
    }
}