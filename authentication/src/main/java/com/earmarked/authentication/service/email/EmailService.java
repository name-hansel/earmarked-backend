package com.earmarked.authentication.service.email;

// TODO: Add @Async annotation to PROD mail service
public interface EmailService {

    void sendAccountActivationEmail(String email, String token);

    void sendAccountActivatedEmail(String email);

    void sendForgotPasswordEmail(String email, String token);

    void sendResetPasswordEmail(String email);
}
