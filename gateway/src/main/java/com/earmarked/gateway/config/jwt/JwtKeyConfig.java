package com.earmarked.gateway.config.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtKeyConfig {

    @Value("classpath:public-key.pem")
    private Resource publicKeyResource;

    @Bean
    public RSAPublicKey publicKey() throws Exception {
        try (InputStream inputStream = publicKeyResource.getInputStream()) {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Certificate certificate = certificateFactory.generateCertificate(inputStream);
            return (RSAPublicKey) certificate.getPublicKey();
        }
    }
}
