// src/main/java/com/academichub/academic_management_hub/controllers/TestController.java

package com.academichub.academic_management_hub.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Public endpoint";
    }

    @GetMapping("/secured")
    public String securedEndpoint() {
        return "Secured endpoint";
    }
}