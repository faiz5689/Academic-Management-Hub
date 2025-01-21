package com.academichub.academic_management_hub.services.impl;

import com.academichub.academic_management_hub.dto.DepartmentDTO;
import com.academichub.academic_management_hub.exceptions.DuplicateEntityException;
import com.academichub.academic_management_hub.exceptions.EntityNotFoundException;
import com.academichub.academic_management_hub.exceptions.InvalidDepartmentOperationException;
import com.academichub.academic_management_hub.models.Department;
import com.academichub.academic_management_hub.models.Professor;
import com.academichub.academic_management_hub.repositories.DepartmentRepository;
import com.academichub.academic_management_hub.repositories.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private UUID departmentId;
    private UUID professorId;
    private UUID differentDepartmentId;
    private Department testDepartment;
    private Department differentDepartment;
    private Professor testProfessor;
    private DepartmentDTO testDepartmentDTO;

    @BeforeEach
    void setUp() {
        departmentId = UUID.randomUUID();
        professorId = UUID.randomUUID();
        differentDepartmentId = UUID.randomUUID();

        testDepartment = Department.builder()
                .id(departmentId)
                .name("Computer Science")
                .description("CS Department")
                .professors(Collections.emptyList())
                .build();

        differentDepartment = Department.builder()
                .id(differentDepartmentId)
                .name("Physics")
                .description("Physics Department")
                .build();

        testProfessor = Professor.builder()
                .id(professorId)
                .firstName("John")
                .lastName("Doe")
                .department(testDepartment)
                .build();

        testDepartmentDTO = DepartmentDTO.builder()
                .name("Computer Science")
                .description("CS Department")
                .build();
    }

    @Test
    void createDepartment_Success() {
        when(departmentRepository.existsByNameIgnoreCase(any())).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenReturn(testDepartment);

        DepartmentDTO result = departmentService.createDepartment(testDepartmentDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(testDepartmentDTO.getName());
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void createDepartment_DuplicateName_ThrowsException() {
        when(departmentRepository.existsByNameIgnoreCase(any())).thenReturn(true);

        assertThatThrownBy(() -> departmentService.createDepartment(testDepartmentDTO))
                .isInstanceOf(DuplicateEntityException.class)
                .hasMessageContaining("Department already exists with name");
    }

    @Test
    void getDepartmentById_Success() {
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(testDepartment));

        DepartmentDTO result = departmentService.getDepartmentById(departmentId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(departmentId);
        assertThat(result.getName()).isEqualTo(testDepartment.getName());
    }

    @Test
    void getDepartmentById_NotFound_ThrowsException() {
        when(departmentRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getDepartmentById(departmentId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Department not found");
    }

    @Test
    void getDepartmentWithAllDetails_Success() {
        when(departmentRepository.findByIdWithAllDetails(departmentId))
                .thenReturn(Optional.of(testDepartment));

        DepartmentDTO result = departmentService.getDepartmentWithAllDetails(departmentId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(departmentId);
    }

    @Test
    void getDepartmentsWithNoHead_Success() {
        when(departmentRepository.findDepartmentsWithNoHead())
                .thenReturn(Arrays.asList(testDepartment));

        var results = departmentService.getDepartmentsWithNoHead();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo(testDepartment.getName());
    }

    @Test
    void updateDepartment_Success() {
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(testDepartment));
        when(departmentRepository.save(any(Department.class))).thenReturn(testDepartment);

        DepartmentDTO updateDTO = DepartmentDTO.builder()
                .name("Updated CS")
                .description("Updated Description")
                .build();

        DepartmentDTO result = departmentService.updateDepartment(departmentId, updateDTO);

        assertThat(result).isNotNull();
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void updateDepartment_DuplicateName_ThrowsException() {
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(testDepartment));
        when(departmentRepository.existsByNameIgnoreCase(any())).thenReturn(true);

        DepartmentDTO updateDTO = DepartmentDTO.builder()
                .name("Physics")
                .description("Updated Description")
                .build();

        assertThatThrownBy(() -> departmentService.updateDepartment(departmentId, updateDTO))
                .isInstanceOf(DuplicateEntityException.class);
    }

    @Test
    void assignHeadProfessor_Success() {
        Professor professorFromSameDepartment = Professor.builder()
                .id(professorId)
                .department(testDepartment)
                .build();

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(testDepartment));
        when(professorRepository.findByIdWithDetails(professorId))
                .thenReturn(Optional.of(professorFromSameDepartment));
        when(departmentRepository.save(any(Department.class))).thenReturn(testDepartment);

        DepartmentDTO result = departmentService.assignHeadProfessor(departmentId, professorId);

        assertThat(result).isNotNull();
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void assignHeadProfessor_ProfessorFromDifferentDepartment_ThrowsException() {
        Professor professorFromDifferentDepartment = Professor.builder()
                .id(professorId)
                .department(differentDepartment)
                .build();

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(testDepartment));
        when(professorRepository.findByIdWithDetails(professorId))
                .thenReturn(Optional.of(professorFromDifferentDepartment));

        assertThatThrownBy(() -> departmentService.assignHeadProfessor(departmentId, professorId))
                .isInstanceOf(InvalidDepartmentOperationException.class)
                .hasMessageContaining("Head professor must belong to the department");
    }

    @Test
    void deleteDepartment_Success() {
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(testDepartment));
        
        departmentService.deleteDepartment(departmentId);
        
        verify(departmentRepository).delete(testDepartment);
    }

    @Test
    void deleteDepartment_WithProfessors_ThrowsException() {
        Department departmentWithProfessors = testDepartment.toBuilder()
                .professors(Arrays.asList(testProfessor))
                .build();

        when(departmentRepository.findById(departmentId))
                .thenReturn(Optional.of(departmentWithProfessors));

        assertThatThrownBy(() -> departmentService.deleteDepartment(departmentId))
                .isInstanceOf(InvalidDepartmentOperationException.class)
                .hasMessageContaining("Cannot delete department that still has professors");
    }
}