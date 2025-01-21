// src/test/java/com/academichub/academic_management_hub/repositories/UserRepositoryTest.java
package com.academichub.academic_management_hub.repositories;

import com.academichub.academic_management_hub.models.User;
import com.academichub.academic_management_hub.models.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByEmail() {
        // Create a test user
        User user = new User();
        user.setEmail("professor@university.edu");
        user.setPasswordHash("hashedPassword");
        user.setRole(UserRole.PROFESSOR);
        
        // Save the user
        entityManager.persist(user);
        entityManager.flush();

        // Find the user by email
        User found = userRepository.findByEmail(user.getEmail()).orElse(null);

        // Verify the user was found and has correct properties
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo(user.getEmail());
        assertThat(found.getRole()).isEqualTo(UserRole.PROFESSOR);
    }

    @Test
    public void testExistsByEmail() {
        // Create a test user
        User user = new User();
        user.setEmail("admin@university.edu");
        user.setPasswordHash("hashedPassword");
        user.setRole(UserRole.ADMIN);
        
        // Save the user
        entityManager.persist(user);
        entityManager.flush();

        // Check if user exists
        boolean exists = userRepository.existsByEmail(user.getEmail());
        boolean doesNotExist = userRepository.existsByEmail("nonexistent@university.edu");

        assertThat(exists).isTrue();
        assertThat(doesNotExist).isFalse();
    }
}