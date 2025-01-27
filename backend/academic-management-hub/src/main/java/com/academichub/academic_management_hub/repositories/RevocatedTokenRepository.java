// RevocatedTokenRepository.java
package com.academichub.academic_management_hub.repositories;

import com.academichub.academic_management_hub.models.RevocatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface RevocatedTokenRepository extends JpaRepository<RevocatedToken, UUID> {
    boolean existsByToken(String token);
}