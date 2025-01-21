package com.academichub.academic_management_hub.repositories;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.academichub.academic_management_hub.models.Department;
import com.academichub.academic_management_hub.models.Professor;
import com.academichub.academic_management_hub.models.ProfessorTitle;
import com.academichub.academic_management_hub.models.User;
import com.academichub.academic_management_hub.models.UserRole;

public class ProfessorRepositoryTest extends RepositoryTestBase {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProfessorRepository professorRepository;

    private User user;
    private Department department;

    @BeforeEach
    public void setUp() {
        // Create and persist a user
        user = new User();
        user.setEmail("professor@university.edu");
        user.setPasswordHash("hashedPassword");
        user.setRole(UserRole.PROFESSOR);
        entityManager.persist(user);

        // Create and persist a department
        department = Department.builder()
                .name("Computer Science")
                .description("CS Department")
                .build();
        entityManager.persist(department);
        
        entityManager.flush();
    }

    @Test
    public void testFindByUserId() {
        Professor professor = Professor.builder()
                .user(user)
                .department(department)
                .firstName("John")
                .lastName("Doe")
                .title(ProfessorTitle.ASSISTANT_PROFESSOR)
                .build();
        professor.setResearchInterestsList(Arrays.asList("AI", "Machine Learning"));
        
        entityManager.persist(professor);
        entityManager.flush();

        Professor found = professorRepository.findByUserId(user.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getUser().getId()).isEqualTo(user.getId());
        assertThat(found.getFirstName()).isEqualTo("John");
        assertThat(found.getResearchInterestsList()).contains("AI", "Machine Learning");
    }

    @Test
    public void testFindByNameContaining() {
        Professor prof1 = Professor.builder()
                .user(user)
                .department(department)
                .firstName("John")
                .lastName("Doe")
                .build();
        entityManager.persist(prof1);

        // Create another user and professor
        User user2 = new User();
        user2.setEmail("johanna@university.edu");
        user2.setPasswordHash("hashedPassword");
        user2.setRole(UserRole.PROFESSOR);
        entityManager.persist(user2);

        Professor prof2 = Professor.builder()
                .user(user2)
                .department(department)
                .firstName("Johanna")
                .lastName("Smith")
                .build();
        entityManager.persist(prof2);

        entityManager.flush();

        List<Professor> foundProfessors = professorRepository.findByNameContaining("joh");

        assertThat(foundProfessors).hasSize(2);
        assertThat(foundProfessors).extracting(Professor::getFirstName)
                .containsExactlyInAnyOrder("John", "Johanna");
    }
}