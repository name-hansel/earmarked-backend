package com.earmarked.authentication.config.jwt;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfiguration {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String REFRESH_TOKEN_HEADER = "X-Refresh-Token";

    public enum TokenType {
        ACCESS_TOKEN, REFRESH_TOKEN;

        public static String accessToken() {
            return ACCESS_TOKEN.name();
        }

        public static String refreshToken() {
            return REFRESH_TOKEN.name();
        }
    }
}
