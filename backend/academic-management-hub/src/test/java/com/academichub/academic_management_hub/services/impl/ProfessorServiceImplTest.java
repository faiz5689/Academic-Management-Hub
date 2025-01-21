package com.academichub.academic_management_hub.services.impl;

import com.academichub.academic_management_hub.dto.ProfessorDTO;
import com.academichub.academic_management_hub.exceptions.EntityNotFoundException;
import com.academichub.academic_management_hub.exceptions.InvalidProfessorOperationException;
import com.academichub.academic_management_hub.models.*;
import com.academichub.academic_management_hub.repositories.DepartmentRepository;
import com.academichub.academic_management_hub.repositories.ProfessorRepository;
import com.academichub.academic_management_hub.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfessorServiceImplTest {

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfessorServiceImpl professorService;

    private UUID professorId;
    private UUID userId;
    private UUID departmentId;
    private UUID newDepartmentId;
    private User testUser;
    private Department testDepartment;
    private Department newDepartment;
    private Professor testProfessor;
    private ProfessorDTO testProfessorDTO;

    @BeforeEach
    void setUp() {
        professorId = UUID.randomUUID();
        userId = UUID.randomUUID();
        departmentId = UUID.randomUUID();
        newDepartmentId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail("professor@university.edu");
        testUser.setRole(UserRole.PROFESSOR);

        testDepartment = Department.builder()
                .id(departmentId)
                .name("Computer Science")
                .build();

        newDepartment = Department.builder()
                .id(newDepartmentId)
                .name("Physics")
                .build();

        testProfessor = Professor.builder()
                .id(professorId)
                .user(testUser)
                .department(testDepartment)
                .firstName("John")
                .lastName("Doe")
                .title(ProfessorTitle.ASSISTANT_PROFESSOR)
                .build();

        testProfessorDTO = ProfessorDTO.builder()
                .userId(userId)
                .departmentId(departmentId)
                .firstName("John")
                .lastName("Doe")
                .title(ProfessorTitle.ASSISTANT_PROFESSOR)
                .build();
    }

    @Test
    void createProfessor_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(testDepartment));
        when(professorRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(professorRepository.save(any(Professor.class))).thenReturn(testProfessor);

        ProfessorDTO result = professorService.createProfessor(testProfessorDTO);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo(testProfessorDTO.getFirstName());
        verify(professorRepository).save(any(Professor.class));
    }

    @Test
    void updateProfessor_Success() {
        // Create an updated DTO with new department
        ProfessorDTO updateDTO = ProfessorDTO.builder()
                .departmentId(departmentId) // Same department
                .firstName("Jane")
                .lastName("Smith")
                .title(ProfessorTitle.ASSISTANT_PROFESSOR)
                .build();

        when(professorRepository.findById(professorId)).thenReturn(Optional.of(testProfessor));
        when(professorRepository.save(any(Professor.class))).thenReturn(testProfessor);

        ProfessorDTO result = professorService.updateProfessor(professorId, updateDTO);

        assertThat(result).isNotNull();
        verify(professorRepository).save(any(Professor.class));
    }

    @Test
    void updateProfessor_DepartmentHead_ThrowsException() {
        // Setup professor as department head
        testDepartment.setHeadProfessor(testProfessor);
        testProfessor.setDepartment(testDepartment);

        // Create update DTO with new department
        ProfessorDTO updateDTO = ProfessorDTO.builder()
                .departmentId(newDepartmentId)
                .firstName("Jane")
                .lastName("Smith")
                .build();

        when(professorRepository.findById(professorId)).thenReturn(Optional.of(testProfessor));
        when(departmentRepository.findById(newDepartmentId)).thenReturn(Optional.of(newDepartment));

        assertThatThrownBy(() -> professorService.updateProfessor(professorId, updateDTO))
                .isInstanceOf(InvalidProfessorOperationException.class)
                .hasMessageContaining("Cannot change department of a professor who is currently department head");
    }

    @Test
    void updateProfessorTitle_ValidProgression_Success() {
        when(professorRepository.findById(professorId)).thenReturn(Optional.of(testProfessor));
        when(professorRepository.save(any(Professor.class))).thenReturn(testProfessor);

        ProfessorDTO result = professorService.updateProfessorTitle(
            professorId, 
            ProfessorTitle.ASSOCIATE_PROFESSOR
        );

        assertThat(result).isNotNull();
        verify(professorRepository).save(any(Professor.class));
    }

    @Test
    void updateProfessorTitle_InvalidProgression_ThrowsException() {
        when(professorRepository.findById(professorId)).thenReturn(Optional.of(testProfessor));

        assertThatThrownBy(() -> 
            professorService.updateProfessorTitle(professorId, ProfessorTitle.DISTINGUISHED_PROFESSOR))
                .isInstanceOf(InvalidProfessorOperationException.class)
                .hasMessageContaining("Invalid title progression");
    }

    @Test
    void getProfessorById_Success() {
        when(professorRepository.findById(professorId)).thenReturn(Optional.of(testProfessor));

        ProfessorDTO result = professorService.getProfessorById(professorId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(professorId);
    }

    @Test
    void getProfessorsByDepartment_Success() {
        when(professorRepository.findByDepartmentId(departmentId))
                .thenReturn(Arrays.asList(testProfessor));

        var results = professorService.getProfessorsByDepartment(departmentId);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDepartmentId()).isEqualTo(departmentId);
    }

    @Test
    void deleteProfessor_Success() {
        when(professorRepository.findById(professorId)).thenReturn(Optional.of(testProfessor));
        
        professorService.deleteProfessor(professorId);
        
        verify(professorRepository).delete(testProfessor);
    }

    @Test
    void deleteProfessor_DepartmentHead_ThrowsException() {
        testDepartment.setHeadProfessor(testProfessor);
        when(professorRepository.findById(professorId)).thenReturn(Optional.of(testProfessor));

        assertThatThrownBy(() -> professorService.deleteProfessor(professorId))
                .isInstanceOf(InvalidProfessorOperationException.class)
                .hasMessageContaining("Cannot delete professor who is currently department head");
    }

    @Test
    void getProfessorById_NotFound_ThrowsException() {
        when(professorRepository.findById(professorId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professorService.getProfessorById(professorId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Professor not found");
    }

    @Test
    void createProfessor_UserNotFound_ThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professorService.createProfessor(testProfessorDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void createProfessor_DepartmentNotFound_ThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(professorRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professorService.createProfessor(testProfessorDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Department not found");
    }

    @Test
    void updateProfessor_ProfessorNotFound_ThrowsException() {
        when(professorRepository.findById(professorId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professorService.updateProfessor(professorId, testProfessorDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Professor not found");
    }
}