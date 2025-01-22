package com.academichub.academic_management_hub.integration;

import com.academichub.academic_management_hub.dto.UserDTO;
import com.academichub.academic_management_hub.models.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerIntegrationTest extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/users";

    private UserDTO createTestUserDTO() {
        return UserDTO.builder()
                .email("test" + System.currentTimeMillis() + "@example.com")
                .role(UserRole.PROFESSOR)
                .isActive(true)
                .build();
    }

    @Test
    void createUser_ValidData_ShouldCreateAndReturnUser() throws Exception {
        // Arrange
        UserDTO requestDto = createTestUserDTO();

        // Act & Assert
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(requestDto.getEmail()))
                .andExpect(jsonPath("$.role").value(requestDto.getRole().name()))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void createUser_InvalidEmail_ShouldReturn400() throws Exception {
        // Arrange
        UserDTO requestDto = UserDTO.builder()
                .email("invalid-email")
                .role(UserRole.PROFESSOR)
                .isActive(true)
                .build();

        // Act & Assert
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getUserById_ExistingUser_ShouldReturnUser() throws Exception {
        // Arrange - First create a user
        UserDTO requestDto = createTestUserDTO();

        MvcResult createResult = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andReturn();

        UserDTO createdUser = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            UserDTO.class
        );

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/{id}", createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUser.getId().toString()))
                .andExpect(jsonPath("$.email").value(createdUser.getEmail()));
    }

    @Test
    void getUserById_NonExistentUser_ShouldReturn404() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", "123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getUserByEmail_ExistingEmail_ShouldReturnUser() throws Exception {
        // Arrange - First create a user
        UserDTO requestDto = createTestUserDTO();

        MvcResult createResult = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        UserDTO createdUser = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            UserDTO.class
        );

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/search")
                .param("email", createdUser.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(createdUser.getEmail()));
    }

    @Test
    void updateUser_ValidData_ShouldUpdateAndReturnUser() throws Exception {
        // Arrange - First create a user
        UserDTO requestDto = createTestUserDTO();

        MvcResult createResult = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andReturn();

        UserDTO createdUser = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            UserDTO.class
        );

        // Update the user using toBuilder
        UserDTO updateDto = createdUser.toBuilder()
                .email("updated" + System.currentTimeMillis() + "@example.com")
                .role(UserRole.ADMIN)
                .build();

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/{id}", createdUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(updateDto.getEmail()))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void toggleUserActive_ShouldToggleStatus() throws Exception {
        // Arrange - First create a user
        UserDTO requestDto = createTestUserDTO();

        MvcResult createResult = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andReturn();

        UserDTO createdUser = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            UserDTO.class
        );

        // Act & Assert - Toggle to inactive
        mockMvc.perform(put(BASE_URL + "/{id}/toggle-active", createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));

        // Toggle back to active
        mockMvc.perform(put(BASE_URL + "/{id}/toggle-active", createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void deleteUser_ExistingUser_ShouldDelete() throws Exception {
        // Arrange - First create a user
        UserDTO requestDto = createTestUserDTO();

        MvcResult createResult = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andReturn();

        UserDTO createdUser = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            UserDTO.class
        );

        // Act & Assert
        mockMvc.perform(delete(BASE_URL + "/{id}", createdUser.getId()))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get(BASE_URL + "/{id}", createdUser.getId()))
                .andExpect(status().isNotFound());
    }
}