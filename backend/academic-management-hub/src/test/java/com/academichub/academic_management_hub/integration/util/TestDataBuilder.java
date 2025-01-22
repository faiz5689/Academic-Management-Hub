package com.academichub.academic_management_hub.integration.util;

import com.academichub.academic_management_hub.dto.DepartmentDTO;
import com.academichub.academic_management_hub.dto.ProfessorDTO;
import com.academichub.academic_management_hub.dto.UserDTO;
import com.academichub.academic_management_hub.models.ProfessorTitle;
import com.academichub.academic_management_hub.models.UserRole;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class TestDataBuilder {
    private static final AtomicInteger counter = new AtomicInteger(0);
    
    public static UserDTO createUserDTO() {
        int count = counter.incrementAndGet();
        return UserDTO.builder()
                .email("test.user" + count + "@example.com")
                .role(UserRole.PROFESSOR)
                .isActive(true)
                .build();
    }

    public static DepartmentDTO createDepartmentDTO() {
        int count = counter.incrementAndGet();
        return DepartmentDTO.builder()
                .name("Test Department " + count)
                .description("Test Description " + count)
                .build();
    }

    public static ProfessorDTO createProfessorDTO(UUID userId, UUID departmentId) {
        int count = counter.incrementAndGet();
        return ProfessorDTO.builder()
                .userId(userId)
                .departmentId(departmentId)
                .firstName("John" + count)
                .lastName("Doe" + count)
                .title(ProfessorTitle.ASSISTANT_PROFESSOR)
                .officeLocation("Room " + count)
                .phone("+1234567890" + count)
                .build();
    }

    public static UserDTO createAdminUserDTO() {
        int count = counter.incrementAndGet();
        return UserDTO.builder()
                .email("admin.user" + count + "@example.com")
                .role(UserRole.ADMIN)
                .isActive(true)
                .build();
    }
}