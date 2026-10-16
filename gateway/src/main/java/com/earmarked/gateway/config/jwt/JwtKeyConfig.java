package com.earmarked.gateway.config.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtKeyConfig {

    @Bean
    public RSAPublicKey jwtPublicKey(@Value("classpath:public-key.pem") Resource resource) throws Exception {
        try (InputStream inputStream = resource.getInputStream()) {
            X509Certificate certificate = (X509Certificate) CertificateFactory.getInstance("X.509")
                    .generateCertificate(inputStream);
            return (RSAPublicKey) certificate.getPublicKey();
        }
    }
}
