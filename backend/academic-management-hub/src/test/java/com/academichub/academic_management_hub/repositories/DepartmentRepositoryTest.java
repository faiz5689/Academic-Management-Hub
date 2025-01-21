package com.academichub.academic_management_hub.repositories;

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

public class DepartmentRepositoryTest extends RepositoryTestBase {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department department1;
    private Department department2;

    @BeforeEach
    public void setUp() {
        // Create test departments
        department1 = Department.builder()
                .name("Computer Science")
                .description("CS Department")
                .build();
        
        department2 = Department.builder()
                .name("Data Science")
                .description("DS Department")
                .build();
        
        entityManager.persist(department1);
        entityManager.persist(department2);
        entityManager.flush();
    }

    @Test
    public void testFindByNameIgnoreCase() {
        Department found = departmentRepository.findByNameIgnoreCase("computer SCIENCE").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo(department1.getName());
        assertThat(found.getDescription()).isEqualTo(department1.getDescription());
    }

    @Test
    public void testFindByNameContainingIgnoreCase() {
        List<Department> departments = departmentRepository.findByNameContainingIgnoreCase("Science");

        assertThat(departments).hasSize(2);
        assertThat(departments).extracting(Department::getName)
                .containsExactlyInAnyOrder("Computer Science", "Data Science");
    }

    @Test
    public void testFindDepartmentsWithNoHead() {
        // Both departments start with no head professor
        List<Department> departmentsWithNoHead = departmentRepository.findDepartmentsWithNoHead();
        assertThat(departmentsWithNoHead).hasSize(2);

        // Create a professor to be department head
        User user = new User();
        user.setEmail("head.prof@university.edu");
        user.setPasswordHash("hashedPassword");
        user.setRole(UserRole.PROFESSOR);
        entityManager.persist(user);

        Professor headProf = Professor.builder()
                .user(user)
                .department(department1)
                .firstName("John")
                .lastName("Doe")
                .title(ProfessorTitle.PROFESSOR)
                .build();
        entityManager.persist(headProf);

        // Assign head professor to department1
        department1.setHeadProfessor(headProf);
        entityManager.persist(department1);
        entityManager.flush();

        // Now only department2 should have no head
        departmentsWithNoHead = departmentRepository.findDepartmentsWithNoHead();
        assertThat(departmentsWithNoHead).hasSize(1);
        assertThat(departmentsWithNoHead.get(0).getId()).isEqualTo(department2.getId());
    }
}