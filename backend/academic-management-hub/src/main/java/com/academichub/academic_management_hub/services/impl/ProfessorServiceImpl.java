package com.academichub.academic_management_hub.services.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academichub.academic_management_hub.dto.ProfessorDTO;
import com.academichub.academic_management_hub.exceptions.EntityNotFoundException;
import com.academichub.academic_management_hub.exceptions.InvalidProfessorOperationException;
import com.academichub.academic_management_hub.models.Department;
import com.academichub.academic_management_hub.models.Professor;
import com.academichub.academic_management_hub.models.ProfessorTitle;
import com.academichub.academic_management_hub.models.User;
import com.academichub.academic_management_hub.repositories.DepartmentRepository;
import com.academichub.academic_management_hub.repositories.ProfessorRepository;
import com.academichub.academic_management_hub.repositories.UserRepository;
import com.academichub.academic_management_hub.services.interfaces.ProfessorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfessorServiceImpl implements ProfessorService {

    private final ProfessorRepository professorRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    @Override
    public ProfessorDTO createProfessor(ProfessorDTO professorDTO) {
        // Validate user exists and isn't already a professor
        User user = findUserById(professorDTO.getUserId());
        if (professorRepository.findByUserId(user.getId()).isPresent()) {
            throw new InvalidProfessorOperationException("User is already associated with a professor");
        }

        // Validate department exists
        Department department = findDepartmentById(professorDTO.getDepartmentId());

        // Create new professor
        Professor professor = Professor.builder()
                .user(user)
                .department(department)
                .firstName(professorDTO.getFirstName())
                .lastName(professorDTO.getLastName())
                .title(professorDTO.getTitle())
                .officeLocation(professorDTO.getOfficeLocation())
                .phone(professorDTO.getPhone())
                .build();

        if (professorDTO.getResearchInterests() != null) {
            professor.setResearchInterestsList(professorDTO.getResearchInterests());
        }

        professor = professorRepository.save(professor);
        return convertToDTO(professor);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfessorDTO getProfessorById(UUID id) {
        Professor professor = findProfessorById(id);
        return convertToDTO(professor);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfessorDTO getProfessorWithDetails(UUID id) {
        Professor professor = professorRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Professor", id));
        return convertToDTO(professor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessorDTO> getAllProfessors() {
        return professorRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessorDTO> getProfessorsByDepartment(UUID departmentId) {
        return professorRepository.findByDepartmentId(departmentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessorDTO> searchProfessorsByName(String query) {
        return professorRepository.findByNameContaining(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessorDTO> getProfessorsByTitle(ProfessorTitle title) {
        return professorRepository.findByTitle(title).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProfessorDTO updateProfessor(UUID id, ProfessorDTO professorDTO) {
        Professor professor = findProfessorById(id);
        Department newDepartment = null;

        // If department is being changed, validate it exists
        if (!professor.getDepartment().getId().equals(professorDTO.getDepartmentId())) {
            newDepartment = findDepartmentById(professorDTO.getDepartmentId());
            
            // Check if professor is head of current department
            if (professor.getDepartment().getHeadProfessor() != null && 
                professor.getDepartment().getHeadProfessor().getId().equals(professor.getId())) {
                throw new InvalidProfessorOperationException(
                    "Cannot change department of a professor who is currently department head"
                );
            }
        }

        professor.setFirstName(professorDTO.getFirstName());
        professor.setLastName(professorDTO.getLastName());
        professor.setTitle(professorDTO.getTitle());
        professor.setOfficeLocation(professorDTO.getOfficeLocation());
        professor.setPhone(professorDTO.getPhone());
        
        if (professorDTO.getResearchInterests() != null) {
            professor.setResearchInterestsList(professorDTO.getResearchInterests());
        }

        if (newDepartment != null) {
            professor.setDepartment(newDepartment);
        }

        professor = professorRepository.save(professor);
        return convertToDTO(professor);
    }

    @Override
    public ProfessorDTO updateProfessorTitle(UUID id, ProfessorTitle newTitle) {
        Professor professor = findProfessorById(id);
        validateTitleProgression(professor.getTitle(), newTitle);
        
        professor.setTitle(newTitle);
        professor = professorRepository.save(professor);
        return convertToDTO(professor);
    }

    @Override
    public void deleteProfessor(UUID id) {
        Professor professor = findProfessorById(id);
        
        // Check if professor is department head
        if (professor.getDepartment().getHeadProfessor() != null && 
            professor.getDepartment().getHeadProfessor().getId().equals(professor.getId())) {
            throw new InvalidProfessorOperationException(
                "Cannot delete professor who is currently department head"
            );
        }

        professorRepository.delete(professor);
    }

    // Helper methods
    private Professor findProfessorById(UUID id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Professor", id));
    }

    private Department findDepartmentById(UUID id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department", id));
    }

    private User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    private void validateTitleProgression(ProfessorTitle currentTitle, ProfessorTitle newTitle) {
        if (currentTitle == null) {
            return;
        }

        boolean validProgression = switch (currentTitle) {
            case ASSISTANT_PROFESSOR -> newTitle == ProfessorTitle.ASSOCIATE_PROFESSOR;
            case ASSOCIATE_PROFESSOR -> newTitle == ProfessorTitle.PROFESSOR;
            case PROFESSOR -> newTitle == ProfessorTitle.DISTINGUISHED_PROFESSOR 
                            || newTitle == ProfessorTitle.EMERITUS_PROFESSOR;
            case DISTINGUISHED_PROFESSOR -> newTitle == ProfessorTitle.EMERITUS_PROFESSOR;
            case EMERITUS_PROFESSOR -> false; // Cannot change from Emeritus
            case VISITING_PROFESSOR, ADJUNCT_PROFESSOR -> true; // Can change to any title
        };

        if (!validProgression) {
            throw new InvalidProfessorOperationException(
                String.format("Invalid title progression from %s to %s", 
                    currentTitle.getDisplayValue(), newTitle.getDisplayValue())
            );
        }
    }

    private ProfessorDTO convertToDTO(Professor professor) {
        return ProfessorDTO.builder()
                .id(professor.getId())
                .userId(professor.getUser().getId())
                .departmentId(professor.getDepartment().getId())
                .firstName(professor.getFirstName())
                .lastName(professor.getLastName())
                .title(professor.getTitle())
                .officeLocation(professor.getOfficeLocation())
                .phone(professor.getPhone())
                .researchInterests(professor.getResearchInterestsList())
                .createdAt(professor.getCreatedAt())
                .updatedAt(professor.getUpdatedAt())
                .build();
    }
}