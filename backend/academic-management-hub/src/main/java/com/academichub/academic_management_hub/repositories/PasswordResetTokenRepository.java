package com.academichub.academic_management_hub.repositories;

import com.academichub.academic_management_hub.models.PasswordResetToken;
import com.academichub.academic_management_hub.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUserAndUsedFalseAndExpiryDateAfter(User user, Instant now);
    void deleteByExpiryDateBeforeAndUsedFalse(Instant expiryDate);
}