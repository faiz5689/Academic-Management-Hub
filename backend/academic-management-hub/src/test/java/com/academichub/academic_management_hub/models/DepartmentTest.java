package com.academichub.academic_management_hub.models;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class DepartmentTest {

    @Test
    public void testDepartmentCreation() {
        Department department = new Department();
        department.setName("Computer Science");
        department.setDescription("Department of Computer Science and Engineering");

        assertEquals("Computer Science", department.getName());
        assertEquals("Department of Computer Science and Engineering", department.getDescription());
        assertNull(department.getHeadProfessor());
        assertNotNull(department.getProfessors());
        assertTrue(department.getProfessors().isEmpty());
    }

    @Test
    public void testDepartmentBuilder() {
        Department department = Department.builder()
                .name("Physics")
                .description("Department of Physics")
                .professors(new ArrayList<>())
                .build();

        assertEquals("Physics", department.getName());
        assertEquals("Department of Physics", department.getDescription());
        assertNotNull(department.getProfessors());
        assertTrue(department.getProfessors().isEmpty());
    }

    @Test
    public void testHeadProfessorAssignment() {
        Department department = new Department();
        Professor headProfessor = new Professor();
        headProfessor.setFirstName("John");
        headProfessor.setLastName("Doe");

        department.setHeadProfessor(headProfessor);

        assertNotNull(department.getHeadProfessor());
        assertEquals("John", department.getHeadProfessor().getFirstName());
        assertEquals("Doe", department.getHeadProfessor().getLastName());
    }
}