// ProfessorService.java
package com.academichub.academic_management_hub.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.academichub.academic_management_hub.dto.ProfessorDTO;
import com.academichub.academic_management_hub.models.ProfessorTitle;

public interface ProfessorService {
    ProfessorDTO createProfessor(ProfessorDTO professorDTO);
    ProfessorDTO getProfessorById(UUID id);
    ProfessorDTO getProfessorWithDetails(UUID id);
    List<ProfessorDTO> getAllProfessors();
    List<ProfessorDTO> getProfessorsByDepartment(UUID departmentId);
    List<ProfessorDTO> searchProfessorsByName(String query);
    List<ProfessorDTO> getProfessorsByTitle(ProfessorTitle title);
    ProfessorDTO updateProfessor(UUID id, ProfessorDTO professorDTO);
    void deleteProfessor(UUID id);
    ProfessorDTO updateProfessorTitle(UUID id, ProfessorTitle newTitle);
}