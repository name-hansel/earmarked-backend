package com.earmarked.authentication.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Profile("resend")
@RequiredArgsConstructor
public class SmtpEmailService implements EmailService {

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final JavaMailSender mailSender;

    @Override
    public void sendAccountActivationEmail(String email, String token) {
        send(email, "Activate your account",
                "Activate your account: " + frontendUrl + "/activate-account?token=" + token);
    }

    @Override
    public void sendAccountActivatedEmail(String email) {
        send(email, "Account activated", "Your account is now active.");
    }

    @Override
    public void sendForgotPasswordEmail(String email, String token) {
        send(email, "Reset your password",
                "Reset your password: " + frontendUrl + "/reset-password?token=" + token);
    }

    @Override
    public void sendResetPasswordEmail(String email) {
        send(email, "Password changed", "Your password was reset successfully.");
    }

    private void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}
