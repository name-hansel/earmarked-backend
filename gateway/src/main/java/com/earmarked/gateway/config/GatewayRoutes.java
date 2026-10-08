package com.earmarked.gateway.config;

import com.earmarked.gateway.config.jwt.JwtConfiguration;
import com.earmarked.gateway.config.jwt.JwtService;
import com.earmarked.gateway.config.services.ServiceBaseUrlProperties;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.server.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.time.Duration;
import java.time.Instant;
import java.util.function.BiFunction;

import static com.earmarked.gateway.config.jwt.JwtConfiguration.ACCESS_TOKEN_COOKIE_PATH;
import static com.earmarked.gateway.config.jwt.JwtConfiguration.REFRESH_TOKEN_COOKIE_PATH;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
@RequiredArgsConstructor
public class GatewayRoutes {
    private final ServiceBaseUrlProperties serviceBaseUrlProperties;
    private final JwtService jwtService;

    @Bean
    public RouterFunction<ServerResponse> authServiceRoute() {
        return route("auth-login")
                .route(path(ApiPaths.LOGIN), http())
                .before(uri(serviceBaseUrlProperties.auth()))
                .after(setAuthCookies())
                .build()
                .and(route("auth-refresh").route(path(ApiPaths.REFRESH), http())
                        .filter(validateAndTranslateRefreshToken())
                        .before(uri(serviceBaseUrlProperties.auth()))
                        .after(setAuthCookies())
                        .build())
                .and(route("auth-logout-delete-account-change-password").route(
                                path(ApiPaths.LOGOUT).or(path(ApiPaths.DELETE_ACCOUNT).or(path(ApiPaths.CHANGE_PASSWORD))),
                                http())
                        .filter(validateAndTranslateAccessToken())
                        .filter(validateAndTranslateRefreshToken())
                        .before(uri(serviceBaseUrlProperties.auth()))
                        .after(clearAuthCookies())
                        .build())
                .and(route("auth-generic").route(path(ApiPaths.AUTH_WILDCARD), http())
                        .filter(validateAndTranslateAccessToken())
                        .before(uri(serviceBaseUrlProperties.auth()))
                        .build());
    }

    private HandlerFilterFunction<ServerResponse, ServerResponse> validateAndTranslateAccessToken() {
        return (request, next) -> {
            var cookie = request.cookies().getFirst(JwtConfiguration.TokenType.accessToken());

            if (cookie != null) {
                try {
                    jwtService.extractClaims(cookie.getValue());
                } catch (JwtException | IllegalArgumentException e) {
                    return getInvalidTokenServerResponse();
                }
            }

            ServerRequest.Builder builder = ServerRequest.from(request)
                    .headers(headers -> headers.remove(HttpHeaders.COOKIE));

            if (cookie != null) {
                builder.header(HttpHeaders.AUTHORIZATION,
                        JwtConfiguration.BEARER_PREFIX + cookie.getValue());
            }

            return next.handle(builder.build());
        };
    }

    private HandlerFilterFunction<ServerResponse, ServerResponse> validateAndTranslateRefreshToken() {
        return (request, next) -> {
            var cookie = request.cookies().getFirst(JwtConfiguration.TokenType.refreshToken());

            if (cookie != null) {
                try {
                    jwtService.extractClaims(cookie.getValue());
                } catch (JwtException | IllegalArgumentException e) {
                    return getInvalidTokenServerResponse();
                }
            }

            ServerRequest.Builder builder = ServerRequest.from(request)
                    .headers(headers -> headers.remove(HttpHeaders.COOKIE));

            if (cookie != null) {
                builder.header(JwtConfiguration.REFRESH_TOKEN_HEADER, cookie.getValue());
            }

            return next.handle(builder.build());
        };
    }

    private BiFunction<ServerRequest, ServerResponse, ServerResponse> setAuthCookies() {
        return (request, response) -> {
            if (!response.statusCode().is2xxSuccessful()) {
                return response;
            }

            HttpHeaders headers = response.headers();
            String authHeader = response.headers().getFirst(HttpHeaders.AUTHORIZATION);
            String refreshHeader = response.headers().getFirst(JwtConfiguration.REFRESH_TOKEN_HEADER);

            headers.remove(HttpHeaders.AUTHORIZATION);
            headers.remove(JwtConfiguration.REFRESH_TOKEN_HEADER);

            if (authHeader != null && authHeader.startsWith(JwtConfiguration.BEARER_PREFIX)) {
                String accessToken = authHeader.substring(JwtConfiguration.BEARER_PREFIX.length());
                Duration maxAge = Duration.between(Instant.now(), jwtService.extractExpiration(accessToken));
                headers.add(HttpHeaders.SET_COOKIE,
                        buildCookie(JwtConfiguration.TokenType.accessToken(), accessToken,
                                ACCESS_TOKEN_COOKIE_PATH,
                                maxAge).toString());
            }

            if (refreshHeader != null) {
                Duration maxAge = Duration.between(Instant.now(), jwtService.extractExpiration(refreshHeader));
                headers.add(HttpHeaders.SET_COOKIE,
                        buildCookie(JwtConfiguration.TokenType.refreshToken(), refreshHeader, REFRESH_TOKEN_COOKIE_PATH,
                                maxAge).toString());
            }

            return response;
        };
    }

    private BiFunction<ServerRequest, ServerResponse, ServerResponse> clearAuthCookies() {
        return (request, response) -> {
            if (!response.statusCode().is2xxSuccessful()) {
                return response;
            }

            ServerResponse.BodyBuilder builder = ServerResponse.from(response);
            builder.header(HttpHeaders.SET_COOKIE,
                    clearCookie(JwtConfiguration.TokenType.accessToken(), ACCESS_TOKEN_COOKIE_PATH).toString());
            builder.header(HttpHeaders.SET_COOKIE,
                    clearCookie(JwtConfiguration.TokenType.refreshToken(), REFRESH_TOKEN_COOKIE_PATH).toString());

            return builder.build();
        };
    }

    private ResponseCookie buildCookie(String name, String value, String path, Duration maxAge) {
        return ResponseCookie.from(name, value).httpOnly(true).secure(true).sameSite(Cookie.SameSite.STRICT.name())
                .path(path).maxAge(maxAge).build();
    }

    private ResponseCookie clearCookie(String name, String path) {
        return buildCookie(name, "", path, Duration.ZERO);
    }

    private record GatewayError(String code, String message) {

    }

    private static ServerResponse getInvalidTokenServerResponse() {
        return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                .body(new GatewayError("INVALID_TOKEN", "Invalid token"));
    }
}
