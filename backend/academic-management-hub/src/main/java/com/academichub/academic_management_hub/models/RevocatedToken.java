// RevocatedToken.java
package com.academichub.academic_management_hub.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "revocated_tokens")
@Getter
@Setter
@NoArgsConstructor
public class RevocatedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false, updatable = false)
    private Instant revokedAt;

    @PrePersist
    protected void onCreate() {
        revokedAt = Instant.now();
    }
}