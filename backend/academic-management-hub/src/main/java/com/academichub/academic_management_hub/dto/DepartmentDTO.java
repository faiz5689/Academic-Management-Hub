// DepartmentDTO.java
package com.academichub.academic_management_hub.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;

@Data
@SuperBuilder(toBuilder = true)  // Changed from @Builder to @SuperBuilder
public class DepartmentDTO {
    private UUID id;
    
    @NotBlank(message = "Department name is required")
    private String name;
    
    private String description;
    private UUID headProfessorId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}