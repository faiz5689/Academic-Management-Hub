package com.academichub.academic_management_hub.integration;

import com.academichub.academic_management_hub.dto.*;
import com.academichub.academic_management_hub.models.*;
import com.academichub.academic_management_hub.repositories.DepartmentRepository;
import com.academichub.academic_management_hub.repositories.ProfessorRepository;
import com.academichub.academic_management_hub.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private Department testDepartment;

    @BeforeEach
    void setUp() {
        super.setUp();
        testDepartment = departmentRepository.save(Department.builder()
            .name("Test Department")
            .description("Test Description")
            .build());
        
        testUser = userRepository.save(User.builder()
            .email("test@example.com")
            .passwordHash(passwordEncoder.encode("password123"))
            .role(UserRole.STAFF)
            .isActive(true)
            .build());
        entityManager.flush();
    }

    @Test
    void loginFlow_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
            loginResult.getResponse().getContentAsString(),
            AuthResponse.class
        );

        assertNotNull(authResponse.getAccessToken());
        assertNotNull(authResponse.getRefreshToken());

        TokenRefreshRequest refreshRequest = new TokenRefreshRequest();
        refreshRequest.setRefreshToken(authResponse.getRefreshToken());

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
            .andExpect(status().isOk())
            .andReturn();

        TokenRefreshResponse refreshResponse = objectMapper.readValue(
            refreshResult.getResponse().getContentAsString(),
            TokenRefreshResponse.class
        );

        assertNotNull(refreshResponse.getAccessToken());
        assertEquals(authResponse.getRefreshToken(), refreshResponse.getRefreshToken());
    }

    @Test
    void passwordReset_Flow_Success() throws Exception {
        ResetPasswordRequest resetRequest = new ResetPasswordRequest();
        resetRequest.setEmail(testUser.getEmail());

        mockMvc.perform(post("/api/auth/password/reset/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetRequest)))
            .andExpect(status().isOk());

        String resetToken = entityManager
            .createQuery(
                "SELECT t.token FROM PasswordResetToken t WHERE t.user.email = :email AND t.used = false",
                String.class)
            .setParameter("email", testUser.getEmail())
            .getSingleResult();

        mockMvc.perform(post("/api/auth/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", resetToken)
                .param("newPassword", "newPassword123"))
            .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testUser.getEmail());
        loginRequest.setPassword("newPassword123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void changePassword_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testUser.getEmail());
        loginRequest.setPassword("password123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
            loginResult.getResponse().getContentAsString(),
            AuthResponse.class
        );

        ChangePasswordRequest changeRequest = new ChangePasswordRequest();
        changeRequest.setCurrentPassword("password123");
        changeRequest.setNewPassword("newPassword123");

        mockMvc.perform(post("/api/auth/password/change")
                .header("Authorization", "Bearer " + authResponse.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeRequest)))
            .andExpect(status().isOk());

        loginRequest.setPassword("newPassword123");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void registerProfessor_Success() throws Exception {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("professor@test.com");
        request.setPassword("Password123@");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDepartmentId(testDepartment.getId());
        request.setTitle(ProfessorTitle.ASSISTANT_PROFESSOR);
        request.setOfficeLocation("Room 101");
        request.setPhone("1234567890");
        request.setResearchInterests(List.of("AI", "Machine Learning"));

        MvcResult result = mockMvc.perform(post("/api/auth/register/professor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn();

        UserDTO response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            UserDTO.class
        );

        assertNotNull(response.getId());
        assertEquals("professor@test.com", response.getEmail());
        assertEquals(UserRole.PROFESSOR, response.getRole());
        assertTrue(response.getIsActive());

        Professor professor = professorRepository.findByUserId(response.getId())
            .orElseThrow(() -> new AssertionError("Professor not found"));
        assertEquals(request.getFirstName(), professor.getFirstName());
        assertEquals(request.getResearchInterests(), professor.getResearchInterestsList());
    }

    @Test
    void registerProfessor_DuplicateEmail_Failure() throws Exception {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail(testUser.getEmail());
        request.setPassword("Password123@");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDepartmentId(testDepartment.getId());
        request.setTitle(ProfessorTitle.ASSISTANT_PROFESSOR);

        mockMvc.perform(post("/api/auth/register/professor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void registerProfessor_InvalidDepartment_Failure() throws Exception {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("newprof@test.com");
        request.setPassword("Password123@");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDepartmentId(UUID.randomUUID());
        request.setTitle(ProfessorTitle.ASSISTANT_PROFESSOR);

        mockMvc.perform(post("/api/auth/register/professor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void registerProfessor_InvalidPassword_Failure() throws Exception {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("newprof@test.com");
        request.setPassword("weak");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDepartmentId(testDepartment.getId());
        request.setTitle(ProfessorTitle.ASSISTANT_PROFESSOR);

        mockMvc.perform(post("/api/auth/register/professor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}