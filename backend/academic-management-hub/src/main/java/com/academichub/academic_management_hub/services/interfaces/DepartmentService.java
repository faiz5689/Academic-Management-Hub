// DepartmentService.java
package com.academichub.academic_management_hub.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.academichub.academic_management_hub.dto.DepartmentDTO;

public interface DepartmentService {
    DepartmentDTO createDepartment(DepartmentDTO departmentDTO);
    DepartmentDTO getDepartmentById(UUID id);
    DepartmentDTO getDepartmentWithAllDetails(UUID id);
    DepartmentDTO removeHeadProfessor(UUID departmentId);
    List<DepartmentDTO> getAllDepartments();
    List<DepartmentDTO> searchDepartmentsByName(String nameFragment);
    List<DepartmentDTO> getDepartmentsWithNoHead();
    DepartmentDTO updateDepartment(UUID id, DepartmentDTO departmentDTO);
    void deleteDepartment(UUID id);
    DepartmentDTO assignHeadProfessor(UUID departmentId, UUID professorId);
}