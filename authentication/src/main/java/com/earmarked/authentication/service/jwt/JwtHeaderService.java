package com.earmarked.authentication.service.jwt;

import com.earmarked.authentication.config.jwt.JwtConfiguration;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class JwtHeaderService {
    public void addAccessTokenHeader(String accessToken, HttpServletResponse response) {
        response.addHeader(HttpHeaders.AUTHORIZATION, JwtConfiguration.BEARER_PREFIX + accessToken);
    }

    public void addRefreshTokenHeader(String refreshToken, HttpServletResponse response) {
        response.addHeader(JwtConfiguration.REFRESH_TOKEN_HEADER, refreshToken);
    }
}
