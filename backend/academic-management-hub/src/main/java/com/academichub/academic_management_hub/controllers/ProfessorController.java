package com.academichub.academic_management_hub.controllers;

import com.academichub.academic_management_hub.dto.ProfessorDTO;
import com.academichub.academic_management_hub.models.ProfessorTitle;
import com.academichub.academic_management_hub.services.interfaces.ProfessorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/professors")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;

    @PostMapping
    public ResponseEntity<ProfessorDTO> createProfessor(@Valid @RequestBody ProfessorDTO professorDTO) {
        ProfessorDTO created = professorService.createProfessor(professorDTO);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessorDTO> getProfessorById(@PathVariable UUID id) {
        ProfessorDTO professor = professorService.getProfessorById(id);
        return ResponseEntity.ok(professor);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<ProfessorDTO> getProfessorWithDetails(@PathVariable UUID id) {
        ProfessorDTO professor = professorService.getProfessorWithDetails(id);
        return ResponseEntity.ok(professor);
    }

    @GetMapping
    public ResponseEntity<List<ProfessorDTO>> getAllProfessors() {
        List<ProfessorDTO> professors = professorService.getAllProfessors();
        return ResponseEntity.ok(professors);
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<ProfessorDTO>> getProfessorsByDepartment(@PathVariable UUID departmentId) {
        List<ProfessorDTO> professors = professorService.getProfessorsByDepartment(departmentId);
        return ResponseEntity.ok(professors);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProfessorDTO>> searchProfessorsByName(@RequestParam String query) {
        List<ProfessorDTO> professors = professorService.searchProfessorsByName(query);
        return ResponseEntity.ok(professors);
    }

    @GetMapping("/title")
    public ResponseEntity<List<ProfessorDTO>> getProfessorsByTitle(@RequestParam ProfessorTitle title) {
        List<ProfessorDTO> professors = professorService.getProfessorsByTitle(title);
        return ResponseEntity.ok(professors);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfessorDTO> updateProfessor(
            @PathVariable UUID id,
            @Valid @RequestBody ProfessorDTO professorDTO
    ) {
        ProfessorDTO updated = professorService.updateProfessor(id, professorDTO);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/title")
    public ResponseEntity<ProfessorDTO> updateProfessorTitle(
            @PathVariable UUID id,
            @RequestParam ProfessorTitle newTitle
    ) {
        ProfessorDTO updated = professorService.updateProfessorTitle(id, newTitle);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessor(@PathVariable UUID id) {
        professorService.deleteProfessor(id);
        return ResponseEntity.noContent().build();
    }
}