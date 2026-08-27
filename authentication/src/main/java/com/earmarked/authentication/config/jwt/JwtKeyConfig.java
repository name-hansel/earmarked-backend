package com.earmarked.authentication.config.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtKeyConfig {

    @Value("${jwt.keystore.location}")
    private Resource keyStoreResource;

    @Value("${jwt.keystore.password}")
    private String keystorePassword;

    @Value("${jwt.keystore.alias}")
    private String keyAlias;

    @Bean
    public KeyStore jwtKeyStore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        try (InputStream inputStream = keyStoreResource.getInputStream()) {
            keyStore.load(inputStream, keystorePassword.toCharArray());
        }

        return keyStore;
    }

    @Bean
    public RSAPrivateKey privateKey(KeyStore jwtKeyStore) throws Exception {
        return (RSAPrivateKey) jwtKeyStore.getKey(keyAlias, keystorePassword.toCharArray());
    }

    @Bean
    public RSAPublicKey publicKey(KeyStore jwtKeyStore) throws Exception {
        return (RSAPublicKey) jwtKeyStore.getCertificate(keyAlias).getPublicKey();
    }

}
