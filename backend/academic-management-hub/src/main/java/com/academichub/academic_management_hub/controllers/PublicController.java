// src/main/java/com/academichub/academic_management_hub/controllers/PublicController.java
package com.academichub.academic_management_hub.controllers;

import com.academichub.academic_management_hub.dto.DepartmentDTO;
import com.academichub.academic_management_hub.services.interfaces.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {
    
    private final DepartmentService departmentService;

    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentDTO>> getPublicDepartmentsList() {
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }
}