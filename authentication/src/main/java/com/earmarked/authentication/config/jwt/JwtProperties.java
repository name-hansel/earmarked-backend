package com.earmarked.authentication.config.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(long accessTokenExpirationSeconds, long refreshTokenExpirationSeconds) {
}