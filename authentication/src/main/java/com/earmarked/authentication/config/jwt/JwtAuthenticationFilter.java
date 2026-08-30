package com.earmarked.authentication.config.jwt;

import com.earmarked.authentication.service.jwt.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = extractAccessToken(request);

        if (token != null) {
            try {
                Authentication authentication = new UsernamePasswordAuthenticationToken(jwtService.extractUserId(token),
                        null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException | IllegalArgumentException e) {
                // Invalid token — leave request unauthenticated
                log.error(e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractAccessToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> JwtConfiguration.TokenType.accessToken().equals(cookie.getName()))
                .map(Cookie::getValue).findFirst().orElse(null);
    }
}
