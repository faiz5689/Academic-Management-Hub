package com.academichub.academic_management_hub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    private UUID id;
    
    @NotBlank(message = "Department name is required")
    private String name;
    
    private String description;
    private UUID headProfessorId;
    private Long professorCount;
    private List<UUID> professorIds;  // Added this field
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}