package com.academichub.academic_management_hub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.experimental.SuperBuilder;

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
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}