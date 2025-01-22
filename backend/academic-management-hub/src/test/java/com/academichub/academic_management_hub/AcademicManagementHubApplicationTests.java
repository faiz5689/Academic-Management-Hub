package com.academichub.academic_management_hub;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.academichub.academic_management_hub.config.SecurityConfig;
import com.academichub.academic_management_hub.config.TestSecurityConfig;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(TestSecurityConfig.class)  // Import the test security config
class AcademicManagementHubApplicationTests {

    @Test
    void contextLoads() {
    }
}