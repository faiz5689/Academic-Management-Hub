package com.academichub.academic_management_hub.models;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProfessorTest {
    
    private User user;
    private Department department;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setEmail("test@test.com");
        user.setPasswordHash("hash");
        user.setRole(UserRole.PROFESSOR);

        department = new Department();
        department.setName("Computer Science");
        department.setDescription("CS Department");
    }

    @Test
    public void testProfessorCreation() {
        Professor professor = new Professor();
        professor.setFirstName("Jane");
        professor.setLastName("Smith");
        professor.setTitle(ProfessorTitle.ASSOCIATE_PROFESSOR);
        professor.setOfficeLocation("Building A, Room 101");
        professor.setPhone("123-456-7890");
        professor.setResearchInterestsList(Arrays.asList("AI", "Machine Learning"));
        professor.setUser(user);
        professor.setDepartment(department);

        assertEquals("Jane", professor.getFirstName());
        assertEquals("Smith", professor.getLastName());
        assertEquals(ProfessorTitle.ASSOCIATE_PROFESSOR, professor.getTitle());
        assertEquals("Building A, Room 101", professor.getOfficeLocation());
        assertEquals("123-456-7890", professor.getPhone());
        assertEquals(2, professor.getResearchInterestsList().size());
        assertTrue(professor.getResearchInterestsList().contains("AI"));
        assertEquals(user, professor.getUser());
        assertEquals(department, professor.getDepartment());
    }

    @Test
    public void testProfessorBuilder() {
        Professor professor = Professor.builder()
                .firstName("Robert")
                .lastName("Johnson")
                .title(ProfessorTitle.PROFESSOR)
                .officeLocation("Building B, Room 202")
                .phone("987-654-3210")
                .user(user)
                .department(department)
                .build();
        
        professor.setResearchInterestsList(Arrays.asList("Database Systems", "Distributed Systems"));

        assertEquals("Robert", professor.getFirstName());
        assertEquals("Johnson", professor.getLastName());
        assertEquals(ProfessorTitle.PROFESSOR, professor.getTitle());
        assertEquals(2, professor.getResearchInterestsList().size());
        assertTrue(professor.getResearchInterestsList().contains("Database Systems"));
        assertEquals(user, professor.getUser());
        assertEquals(department, professor.getDepartment());
    }

    @Test
    public void testResearchInterestsListHandling() {
        Professor professor = new Professor();
        
        // Test empty list
        professor.setResearchInterestsList(List.of());
        assertTrue(professor.getResearchInterestsList().isEmpty());
        
        // Test null list
        professor.setResearchInterestsList(null);
        assertTrue(professor.getResearchInterestsList().isEmpty());
        
        // Test single item
        professor.setResearchInterestsList(List.of("AI"));
        assertEquals(1, professor.getResearchInterestsList().size());
        assertEquals("AI", professor.getResearchInterestsList().get(0));
        
        // Test multiple items
        List<String> interests = Arrays.asList("AI", "Machine Learning", "Data Science");
        professor.setResearchInterestsList(interests);
        assertEquals(3, professor.getResearchInterestsList().size());
        assertTrue(professor.getResearchInterestsList().containsAll(interests));
    }
}