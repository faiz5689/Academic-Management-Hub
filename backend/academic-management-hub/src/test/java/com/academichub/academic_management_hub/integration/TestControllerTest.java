// src/test/java/com/academichub/academic_management_hub/integration/TestControllerTest.java

package com.academichub.academic_management_hub.integration;

import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TestControllerTest extends BaseIntegrationTest {

    @Test
    void publicEndpoint_ShouldBeAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/test/public"))
               .andExpect(status().isOk())
               .andExpect(content().string("Public endpoint"));
    }

    @Test
    void securedEndpoint_ShouldRequireAuth() throws Exception {
        mockMvc.perform(get("/api/test/secured"))
               .andExpect(status().isOk())
               .andExpect(content().string("Secured endpoint"));
    }
}