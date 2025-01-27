package com.academichub.academic_management_hub.controllers;

import com.academichub.academic_management_hub.dto.DepartmentDTO;
import com.academichub.academic_management_hub.services.interfaces.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentDTO> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {
        DepartmentDTO created = departmentService.createDepartment(departmentDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/public/departments")
    public ResponseEntity<List<DepartmentDTO>> getPublicDepartmentsList() {
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable UUID id) {
        DepartmentDTO department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<DepartmentDTO> getDepartmentWithAllDetails(@PathVariable UUID id) {
        DepartmentDTO department = departmentService.getDepartmentWithAllDetails(id);
        return ResponseEntity.ok(department);
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/search")
    public ResponseEntity<List<DepartmentDTO>> searchDepartmentsByName(@RequestParam String nameFragment) {
        List<DepartmentDTO> results = departmentService.searchDepartmentsByName(nameFragment);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/headless")
    public ResponseEntity<List<DepartmentDTO>> getDepartmentsWithNoHead() {
        List<DepartmentDTO> results = departmentService.getDepartmentsWithNoHead();
        return ResponseEntity.ok(results);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDTO> updateDepartment(
            @PathVariable UUID id,
            @Valid @RequestBody DepartmentDTO departmentDTO) {
        DepartmentDTO updated = departmentService.updateDepartment(id, departmentDTO);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{departmentId}/remove-head")
    public ResponseEntity<DepartmentDTO> removeHeadProfessor(@PathVariable UUID departmentId) {
        DepartmentDTO updated = departmentService.removeHeadProfessor(departmentId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable UUID id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{departmentId}/assign-head/{professorId}")
    public ResponseEntity<DepartmentDTO> assignHeadProfessor(
            @PathVariable UUID departmentId,
            @PathVariable UUID professorId) {
        DepartmentDTO updated = departmentService.assignHeadProfessor(departmentId, professorId);
        return ResponseEntity.ok(updated);
    }
}