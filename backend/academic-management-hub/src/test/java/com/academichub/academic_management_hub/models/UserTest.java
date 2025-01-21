// src/test/java/com/academichub/academic_management_hub/models/UserTest.java
package com.academichub.academic_management_hub.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class UserTest {
    
    @Test
    public void testUserCreation() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user.setRole(UserRole.PROFESSOR);
        
        assertEquals("test@example.com", user.getEmail());
        assertEquals("hashedPassword", user.getPasswordHash());
        assertEquals(UserRole.PROFESSOR, user.getRole());
        assertTrue(user.getIsActive());
    }
}