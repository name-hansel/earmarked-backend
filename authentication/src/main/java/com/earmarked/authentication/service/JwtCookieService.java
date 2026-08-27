package com.earmarked.authentication.service;

import com.earmarked.authentication.config.ApiPaths;
import com.earmarked.authentication.config.jwt.JwtConfiguration;
import com.earmarked.authentication.config.jwt.JwtProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class JwtCookieService {
    private final JwtProperties jwtProperties;

    public void addAccessTokenCookie(String accessToken, HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                buildCookie(JwtConfiguration.TokenType.ACCESS_TOKEN, accessToken, "/",
                        jwtProperties.accessTokenExpirationSeconds()).toString());
    }

    public void addRefreshTokenCookie(String refreshToken, HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                buildCookie(JwtConfiguration.TokenType.REFRESH_TOKEN, refreshToken, ApiPaths.AUTH,
                        jwtProperties.refreshTokenExpirationSeconds()).toString());
    }

    private ResponseCookie buildCookie(JwtConfiguration.TokenType tokenType, String token, String path, long expirationSeconds) {
        return ResponseCookie.from(String.valueOf(tokenType), token).httpOnly(true)
                .secure(true).sameSite(String.valueOf(Cookie.SameSite.STRICT)).path(path)
                .maxAge(Duration.ofSeconds(expirationSeconds)).build();
    }
}
