package com.academichub.academic_management_hub.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")  // Note: Changed from "/api/test"
    public String test() {
        return "Backend is working!";
    }
}