package com.academichub.academic_management_hub.integration;

import com.academichub.academic_management_hub.dto.DepartmentDTO;
import com.academichub.academic_management_hub.dto.ProfessorDTO;
import com.academichub.academic_management_hub.dto.UserDTO;
import com.academichub.academic_management_hub.models.ProfessorTitle;
import com.academichub.academic_management_hub.models.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

class ProfessorControllerIntegrationTest extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/professors";
    private UUID userId;
    private UUID departmentId;

    @BeforeEach
    void setUp() {
        super.setUp();
        try {
            userId = createTestUser();
            departmentId = createTestDepartment();
        } catch (Exception e) {
            fail("Failed to set up test data: " + e.getMessage());
        }
    }

    private UUID createTestUser() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .email("professor" + System.currentTimeMillis() + "@test.com")
                .role(UserRole.PROFESSOR)
                .isActive(true)
                .build();

        MvcResult result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        UserDTO createdUser = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            UserDTO.class
        );
        return createdUser.getId();
    }

    private UUID createTestDepartment() throws Exception {
        DepartmentDTO departmentDTO = DepartmentDTO.builder()
                .name("Test Department " + System.currentTimeMillis())
                .description("Test Description")
                .build();

        MvcResult result = mockMvc.perform(post("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        DepartmentDTO createdDepartment = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            DepartmentDTO.class
        );
        return createdDepartment.getId();
    }

    private ProfessorDTO createTestProfessorDTO() {
        return ProfessorDTO.builder()
                .userId(userId)
                .departmentId(departmentId)
                .firstName("John")
                .lastName("Doe")
                .title(ProfessorTitle.ASSISTANT_PROFESSOR)
                .officeLocation("Room 101")
                .phone("+1234567890")
                .researchInterests(Arrays.asList("AI", "Machine Learning"))
                .build();
    }

    @Test
    void createProfessor_ValidData_ShouldCreateAndReturnProfessor() throws Exception {
        // Arrange
        ProfessorDTO requestDto = createTestProfessorDTO();

        // Act & Assert
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.title").value("ASSISTANT_PROFESSOR"))
                .andExpect(jsonPath("$.departmentId").value(departmentId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()));
    }

    @Test
    void createProfessor_InvalidData_ShouldReturn400() throws Exception {
        // Arrange - Missing required fields
        ProfessorDTO invalidDto = ProfessorDTO.builder()
                .title(ProfessorTitle.ASSISTANT_PROFESSOR)
                .build();

        // Act & Assert
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void getProfessorById_ExistingProfessor_ShouldReturnProfessor() throws Exception {
        // Arrange - First create a professor
        ProfessorDTO requestDto = createTestProfessorDTO();

        MvcResult createResult = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andReturn();

        ProfessorDTO createdProfessor = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            ProfessorDTO.class
        );

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/{id}", createdProfessor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdProfessor.getId().toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void updateProfessor_ValidData_ShouldUpdateAndReturnProfessor() throws Exception {
        // Arrange - First create a professor
        ProfessorDTO requestDto = createTestProfessorDTO();

        MvcResult createResult = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andReturn();

        ProfessorDTO createdProfessor = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            ProfessorDTO.class
        );

        // Update the professor
        ProfessorDTO updateDto = createdProfessor.toBuilder()
                .firstName("Jane")
                .lastName("Smith")
                .officeLocation("Room 102")
                .phone("+1987654321")
                .build();

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/{id}", createdProfessor.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.officeLocation").value("Room 102"))
                .andExpect(jsonPath("$.phone").value("+1987654321"));
    }

    @Test
    void getProfessorsByDepartment_ShouldReturnList() throws Exception {
        // Arrange - Create a professor
        ProfessorDTO requestDto = createTestProfessorDTO();

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/department/{departmentId}", departmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].departmentId").value(departmentId.toString()));
    }

    @Test
    void getProfessorsByTitle_ShouldReturnList() throws Exception {
        // Arrange - Create a professor
        ProfessorDTO requestDto = createTestProfessorDTO();

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/title")
                .param("title", ProfessorTitle.ASSISTANT_PROFESSOR.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("ASSISTANT_PROFESSOR"));
    }

    @Test
    void updateProfessorTitle_ValidProgression_ShouldUpdateTitle() throws Exception {
        // Arrange - First create a professor
        ProfessorDTO requestDto = createTestProfessorDTO();

        MvcResult createResult = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andReturn();

        ProfessorDTO createdProfessor = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            ProfessorDTO.class
        );

        // Act & Assert - Promote to Associate Professor (valid step)
        mockMvc.perform(put(BASE_URL + "/{id}/title", createdProfessor.getId())
                .param("newTitle", ProfessorTitle.ASSOCIATE_PROFESSOR.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("ASSOCIATE_PROFESSOR"));
    }

    @Test
    void updateProfessorTitle_InvalidProgression_ShouldReturn400() throws Exception {
        // Arrange - First create a professor
        ProfessorDTO requestDto = createTestProfessorDTO();

        MvcResult createResult = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andReturn();

        ProfessorDTO createdProfessor = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            ProfessorDTO.class
        );

        // Act & Assert - Try to promote directly to Professor (invalid step)
        mockMvc.perform(put(BASE_URL + "/{id}/title", createdProfessor.getId())
                .param("newTitle", ProfessorTitle.PROFESSOR.name()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid title progression from Assistant Professor to Professor"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void deleteProfessor_ExistingProfessor_ShouldDelete() throws Exception {
        // Arrange - First create a professor
        ProfessorDTO requestDto = createTestProfessorDTO();

        MvcResult createResult = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andReturn();

        ProfessorDTO createdProfessor = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            ProfessorDTO.class
        );

        // Act & Assert
        mockMvc.perform(delete(BASE_URL + "/{id}", createdProfessor.getId()))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get(BASE_URL + "/{id}", createdProfessor.getId()))
                .andExpect(status().isNotFound());
    }
}