package com.earmarked.gateway.config;

public class JwtConfiguration {
    public static final String REFRESH_TOKEN_COOKIE = "REFRESH_TOKEN";

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
