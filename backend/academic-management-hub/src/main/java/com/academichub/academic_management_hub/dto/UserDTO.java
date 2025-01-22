package com.academichub.academic_management_hub.dto;

import com.academichub.academic_management_hub.models.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotNull(message = "User role is required")
    private UserRole role;
    
    private Boolean isActive = true;
    
    private ZonedDateTime createdAt;
    private ZonedDateTime lastLogin;
}