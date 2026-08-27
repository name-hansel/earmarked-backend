package com.earmarked.authentication.config.jwt;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfiguration {
    public enum TokenType {
        ACCESS_TOKEN,
        REFRESH_TOKEN
    }
}
