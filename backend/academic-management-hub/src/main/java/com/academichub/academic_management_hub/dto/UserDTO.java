// UserDTO.java
package com.academichub.academic_management_hub.dto;

import com.academichub.academic_management_hub.models.UserRole;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import java.time.ZonedDateTime;
import java.util.UUID;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@SuperBuilder(toBuilder = true)  // Changed from @Builder to @SuperBuilder
public class UserDTO {
    private UUID id;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotNull(message = "User role is required")
    private UserRole role;
    
    private Boolean isActive;
    private ZonedDateTime createdAt;
    private ZonedDateTime lastLogin;
}