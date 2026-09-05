package com.earmarked.authentication.service.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("local")
@Slf4j
// TODO: Add @Async annotation
public class LocalEmailService implements EmailService {
    private static final String LOCAL_FRONTEND = "http://localhost:3000";
    private static final String LOCAL_FRONTEND_URL_ACCOUNT_ACTIVATION = LOCAL_FRONTEND + "/activate-account?token=";
    private static final String LOCAL_FRONTEND_URL_RESET_PASSWORD = LOCAL_FRONTEND + "/reset-password?token=";

    @Override
    public void sendAccountActivationEmail(String email, String token) {
        log.info("""
                Account activation email
                To: {}
                Activation URL: {}
                """, email, LOCAL_FRONTEND_URL_ACCOUNT_ACTIVATION + token);
    }

    @Override
    public void sendAccountActivatedEmail(String email) {
        log.info("""
                Account activated email
                To: {}
                """, email);
    }

    @Override
    public void sendForgotPasswordEmail(String email, String token) {
        log.info("""
                Forgot password email
                To: {}
                Password reset URL: {}
                """, email, LOCAL_FRONTEND_URL_RESET_PASSWORD + token);
    }

    @Override
    public void sendResetPasswordEmail(String email) {
        log.info("""
                Password reset successfully email
                To: {}
                """, email);
    }
}
