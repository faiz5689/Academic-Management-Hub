package com.academichub.academic_management_hub.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academichub.academic_management_hub.models.TestEntity;
import com.academichub.academic_management_hub.repositories.TestRepository;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private TestRepository testRepository;

    @GetMapping("/db")
    public ResponseEntity<String> testDatabase() {
        try {
            // Create a test entity
            TestEntity test = new TestEntity();
            test.setName("Test Entry");
            
            // Save it to database
            testRepository.save(test);
            
            // Retrieve all entries
            long count = testRepository.count();
            
            return ResponseEntity.ok("Database connection successful! Number of entries: " + count);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("Database error: " + e.getMessage());
        }
    }
}