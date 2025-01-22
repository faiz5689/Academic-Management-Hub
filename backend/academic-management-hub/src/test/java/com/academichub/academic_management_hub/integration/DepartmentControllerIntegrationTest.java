package com.academichub.academic_management_hub.integration;

import com.academichub.academic_management_hub.AcademicManagementHubApplication;
import com.academichub.academic_management_hub.dto.DepartmentDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = AcademicManagementHubApplication.class)
class DepartmentControllerIntegrationTest extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/departments";

    @Test
    void createDepartment_ValidData_ShouldCreateAndReturnDepartment() throws Exception {
        // Arrange
        DepartmentDTO requestDto = DepartmentDTO.builder()
                .name("Test Department")
                .description("Test Description")
                .build();

        // Act & Assert
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(requestDto.getName()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void getDepartmentById_NonExistentDepartment_ShouldReturn404() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", "123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void createDepartment_InvalidData_ShouldReturn400() throws Exception {
        // Arrange - Create invalid DTO without required name
        DepartmentDTO invalidDto = DepartmentDTO.builder()
                .description("Test Description")
                .build();

        // Act & Assert
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.name").exists());
    }
}