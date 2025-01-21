package com.academichub.academic_management_hub.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.academichub.academic_management_hub.models.Professor;
import com.academichub.academic_management_hub.models.ProfessorTitle;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, UUID> {
    
    Optional<Professor> findByUserId(UUID userId);
    
    List<Professor> findByDepartmentId(UUID departmentId);
    
    List<Professor> findByTitle(ProfessorTitle title);
    
    @Query("SELECT p FROM Professor p WHERE " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Professor> findByNameContaining(@Param("query") String query);
    
    @Query("SELECT p FROM Professor p " +
           "LEFT JOIN FETCH p.user u " +
           "LEFT JOIN FETCH p.department d " +
           "WHERE p.id = :id")
    Optional<Professor> findByIdWithDetails(@Param("id") UUID id);
    
    boolean existsByUserIdAndDepartmentId(UUID userId, UUID departmentId);
}