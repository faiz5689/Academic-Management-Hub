package com.academichub.academic_management_hub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.academichub.academic_management_hub.models.TestEntity;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long> {
}