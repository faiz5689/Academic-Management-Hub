package com.academichub.academic_management_hub.services.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Collections;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academichub.academic_management_hub.dto.DepartmentDTO;
import com.academichub.academic_management_hub.exceptions.DuplicateEntityException;
import com.academichub.academic_management_hub.exceptions.EntityNotFoundException;
import com.academichub.academic_management_hub.exceptions.InvalidDepartmentOperationException;
import com.academichub.academic_management_hub.models.Department;
import com.academichub.academic_management_hub.models.Professor;
import com.academichub.academic_management_hub.repositories.DepartmentRepository;
import com.academichub.academic_management_hub.repositories.ProfessorRepository;
import com.academichub.academic_management_hub.services.interfaces.DepartmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final ProfessorRepository professorRepository;

    @Override
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        // Check if department with same name exists
        if (departmentRepository.existsByNameIgnoreCase(departmentDTO.getName())) {
            throw new DuplicateEntityException("Department", "name", departmentDTO.getName());
        }

        // Create new department
        Department department = Department.builder()
                .name(departmentDTO.getName())
                .description(departmentDTO.getDescription())
                .build();

        department = departmentRepository.save(department);
        return convertToDTO(department);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDTO getDepartmentById(UUID id) {
        Department department = findDepartmentById(id);
        return convertToDTO(department);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDTO getDepartmentWithAllDetails(UUID id) {
        Department department = departmentRepository.findByIdWithAllDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Department", id));
        return convertToDTOWithDetails(department);
    }

    @Override
    public DepartmentDTO removeHeadProfessor(UUID departmentId) {
        Department department = findDepartmentById(departmentId);
        department.setHeadProfessor(null);
        department = departmentRepository.save(department);
        return convertToDTO(department);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDTO> getAllDepartments() {
        List<Object[]> departmentsWithCounts = departmentRepository.findAllWithProfessorCount();
        return departmentsWithCounts.stream()
                .map(result -> {
                    Department department = (Department) result[0];
                    Long count = (Long) result[1];
                    return convertToDTO(department, count);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDTO> searchDepartmentsByName(String nameFragment) {
        return departmentRepository.findByNameContainingIgnoreCase(nameFragment).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDTO> getDepartmentsWithNoHead() {
        return departmentRepository.findDepartmentsWithNoHead().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentDTO updateDepartment(UUID id, DepartmentDTO departmentDTO) {
        Department department = findDepartmentById(id);

        // Check name uniqueness if name is being changed
        if (!department.getName().equalsIgnoreCase(departmentDTO.getName()) &&
                departmentRepository.existsByNameIgnoreCase(departmentDTO.getName())) {
            throw new DuplicateEntityException("Department", "name", departmentDTO.getName());
        }

        department.setName(departmentDTO.getName());
        department.setDescription(departmentDTO.getDescription());

        department = departmentRepository.save(department);
        return convertToDTO(department);
    }

    @Override
    public DepartmentDTO assignHeadProfessor(UUID departmentId, UUID professorId) {
        Department department = findDepartmentById(departmentId);
        Professor professor = professorRepository.findByIdWithDetails(professorId)
                .orElseThrow(() -> new EntityNotFoundException("Professor", professorId));

        // Validate professor belongs to the department
        if (!professor.getDepartment().getId().equals(departmentId)) {
            throw new InvalidDepartmentOperationException(
                "Head professor must belong to the department they will lead"
            );
        }

        department.setHeadProfessor(professor);
        department = departmentRepository.save(department);
        return convertToDTO(department);
    }

    @Override
    public void deleteDepartment(UUID id) {
        Department department = findDepartmentById(id);
        
        // Check if department has any professors
        if (!department.getProfessors().isEmpty()) {
            throw new InvalidDepartmentOperationException(
                "Cannot delete department that still has professors assigned"
            );
        }

        departmentRepository.delete(department);
    }

    // Helper methods
    private Department findDepartmentById(UUID id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department", id));
    }

    private DepartmentDTO convertToDTO(Department department, Long professorCount) {
        return DepartmentDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .headProfessorId(department.getHeadProfessor() != null ? 
                    department.getHeadProfessor().getId() : null)
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .professorCount(professorCount)
                .build();
    }

    private DepartmentDTO convertToDTO(Department department) {
        return convertToDTO(department, 
            departmentRepository.countProfessorsByDepartmentId(department.getId()));
    }

    private DepartmentDTO convertToDTOWithDetails(Department department) {
        List<UUID> professorIds = department.getProfessors() != null 
            ? department.getProfessors().stream()
                .map(Professor::getId)
                .collect(Collectors.toList())
            : Collections.emptyList();

        return DepartmentDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .headProfessorId(department.getHeadProfessor() != null ? 
                    department.getHeadProfessor().getId() : null)
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .professorCount((long) professorIds.size())
                .professorIds(professorIds)
                .build();
    }
}