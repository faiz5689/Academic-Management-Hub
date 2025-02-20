package com.academichub.academic_management_hub.dto;

import com.academichub.academic_management_hub.models.ProfessorTitle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorDTO {
    private UUID id;
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotNull(message = "Department ID is required")
    private UUID departmentId;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    private ProfessorTitle title;
    private String officeLocation;
    private String phone;
    private List<String> researchInterests;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}