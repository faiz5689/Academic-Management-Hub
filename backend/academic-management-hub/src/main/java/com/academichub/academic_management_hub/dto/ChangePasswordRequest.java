// ChangePasswordRequest.java
package com.academichub.academic_management_hub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank
    private String currentPassword;
    
    @NotBlank
    private String newPassword;
}