package com.earmarked.authentication.service.email;

public interface EmailService {

    void sendAccountActivationEmail(String email, String token);

    void sendAccountActivatedEmail(String email);
}
