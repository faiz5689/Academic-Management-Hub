package com.academichub.academic_management_hub.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.academichub.academic_management_hub.models.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    
    Optional<Department> findByNameIgnoreCase(String name);
    
    boolean existsByNameIgnoreCase(String name);
    
    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.headProfessor WHERE d.id = :id")
    Optional<Department> findByIdWithHeadProfessor(@Param("id") UUID id);
    
    @Query("SELECT d FROM Department d WHERE d.headProfessor IS NULL")
    List<Department> findDepartmentsWithNoHead();
    
    @Query("SELECT DISTINCT d FROM Department d " +
           "LEFT JOIN FETCH d.professors " +
           "LEFT JOIN FETCH d.headProfessor " +
           "WHERE d.id = :id")
    Optional<Department> findByIdWithAllDetails(@Param("id") UUID id);
    
    List<Department> findByNameContainingIgnoreCase(String nameFragment);
}