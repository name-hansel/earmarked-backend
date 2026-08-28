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
    private static final String ACCESS_TOKEN_COOKIE_PATH = "/";
    private static final String REFRESH_TOKEN_COOKIE_PATH = ApiPaths.AUTH;

    public void addAccessTokenCookie(String accessToken, HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                buildCookie(JwtConfiguration.TokenType.accessToken(), accessToken, ACCESS_TOKEN_COOKIE_PATH,
                        jwtProperties.accessTokenExpirationSeconds()).toString());
    }

    public void addRefreshTokenCookie(String refreshToken, HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                buildCookie(JwtConfiguration.TokenType.refreshToken(), refreshToken, REFRESH_TOKEN_COOKIE_PATH,
                        jwtProperties.refreshTokenExpirationSeconds()).toString());
    }

    public void clearCookies(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                clearCookie(JwtConfiguration.TokenType.accessToken(), ACCESS_TOKEN_COOKIE_PATH).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                clearCookie(JwtConfiguration.TokenType.refreshToken(), REFRESH_TOKEN_COOKIE_PATH).toString());
    }

    private ResponseCookie clearCookie(String cookieName, String path) {
        return buildCookie(cookieName, "", path, 0);
    }

    private ResponseCookie buildCookie(String cookieName, String token, String path, long expirationSeconds) {
        return ResponseCookie.from(cookieName, token).httpOnly(true).secure(true)
                .sameSite(Cookie.SameSite.STRICT.name())
                .path(path).maxAge(Duration.ofSeconds(expirationSeconds)).build();
    }
}
