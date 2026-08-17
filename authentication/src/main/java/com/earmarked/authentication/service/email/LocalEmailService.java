package com.earmarked.authentication.service.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("local")
@Slf4j
public class LocalEmailService implements EmailService {
    private static final String LOCAL_FRONTEND_URL_ACCOUNT_ACTIVATION = "http://localhost:3000/activate-account?token=";

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
}
