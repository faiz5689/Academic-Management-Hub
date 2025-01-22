package com.academichub.academic_management_hub.services.interfaces;

public interface EmailService {
    void sendPasswordResetEmail(String to, String token);
}