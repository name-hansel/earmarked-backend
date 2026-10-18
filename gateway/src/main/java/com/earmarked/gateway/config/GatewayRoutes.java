package com.earmarked.gateway.config;

import com.earmarked.gateway.config.jwt.JwtConfiguration;
import com.earmarked.gateway.config.jwt.JwtService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.server.Cookie;
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
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
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
@RequiredArgsConstructor
public class GatewayRoutes {
    private final JwtService jwtService;

    @Bean
    public RouterFunction<ServerResponse> authServiceRoute() {
        return route("auth-login")
                .route(path(ApiPaths.LOGIN), http())
                .after(setAuthCookies())
                .filter(LoadBalancerFilterFunctions.lb(ServiceNames.AUTHENTICATION))
                .build()
                .and(route("auth-refresh")
                        .route(path(ApiPaths.REFRESH), http())
                        .filter(validateAndTranslateRefreshToken())
                        .after(setAuthCookies())
                        .filter(LoadBalancerFilterFunctions.lb(ServiceNames.AUTHENTICATION))
                        .build())
                .and(route("auth-logout-delete-account")
                        .route(path(ApiPaths.LOGOUT)
                                .or(path(ApiPaths.DELETE_ACCOUNT)), http())
                        .filter(validateAndTranslateAccessToken())
                        .filter(validateAndTranslateRefreshToken())
                        .after(clearAuthCookies())
                        .filter(LoadBalancerFilterFunctions.lb(ServiceNames.AUTHENTICATION))
                        .build())
                .and(route("auth-change-password")
                        .route(path(ApiPaths.CHANGE_PASSWORD), http())
                        .filter(validateAndTranslateAccessToken())
                        .filter(validateAndTranslateRefreshToken())
                        .filter(LoadBalancerFilterFunctions.lb(ServiceNames.AUTHENTICATION))
                        .build())
                .and(route("auth-generic")
                        .route(path(ApiPaths.AUTH_WILDCARD), http())
                        .filter(validateAndTranslateAccessToken())
                        .filter(LoadBalancerFilterFunctions.lb(ServiceNames.AUTHENTICATION))
                        .build());
    }

    @Bean
    public RouterFunction<ServerResponse> spotifyServiceRoutes() {
        return route("spotify")
                .route(path(ApiPaths.SPOTIFY_WILDCARD), http())
                .filter(requireAccessToken())
                .filter(validateAndTranslateAccessToken())
                .filter(LoadBalancerFilterFunctions.lb(ServiceNames.SPOTIFY))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> earmarkServiceRoutes() {
        return route("earmark")
                .route(path(ApiPaths.EARMARK_WILDCARD), http())
                .filter(validateAndTranslateAccessToken())
                .filter(LoadBalancerFilterFunctions.lb(ServiceNames.EARMARK))
                .build();
    }

    private HandlerFilterFunction<ServerResponse, ServerResponse> validateAndTranslateAccessToken() {
        return tokenFilter(JwtConfiguration.TokenType.accessToken(), HttpHeaders.AUTHORIZATION,
                JwtConfiguration.BEARER_PREFIX);
    }

    private HandlerFilterFunction<ServerResponse, ServerResponse> validateAndTranslateRefreshToken() {
        return tokenFilter(JwtConfiguration.TokenType.refreshToken(), JwtConfiguration.REFRESH_TOKEN_HEADER, "");
    }

    private HandlerFilterFunction<ServerResponse, ServerResponse> tokenFilter(String tokenType, String cookieHeaderName, String cookieValuePrefix) {
        return (request, next) -> {
            jakarta.servlet.http.Cookie cookie = request.cookies().getFirst(tokenType);

            if (cookie != null) {
                try {
                    jwtService.extractClaims(cookie.getValue());
                } catch (JwtException | IllegalArgumentException e) {
                    return getInvalidTokenServerResponse();
                }
            }

            ServerRequest.Builder builder = ServerRequest.from(request).headers(headers -> {
                headers.remove(HttpHeaders.COOKIE);
                headers.remove(cookieHeaderName);
            });

            if (cookie != null) {
                builder.header(cookieHeaderName, cookieValuePrefix + cookie.getValue());
            }

            return next.handle(builder.build());
        };
    }

    private HandlerFilterFunction<ServerResponse, ServerResponse> requireAccessToken() {
        return (request, next) -> {
            if (request.cookies().getFirst(JwtConfiguration.TokenType.accessToken()) == null) {
                return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                        .body(new GatewayError("MISSING_TOKEN", "Authentication required"));
            }

            return next.handle(request);
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
                headers.add(HttpHeaders.SET_COOKIE,
                        buildCookie(JwtConfiguration.TokenType.accessToken(), accessToken, ACCESS_TOKEN_COOKIE_PATH,
                                remainingTtl(accessToken)).toString());
            }

            if (refreshHeader != null) {
                headers.add(HttpHeaders.SET_COOKIE,
                        buildCookie(JwtConfiguration.TokenType.refreshToken(), refreshHeader, REFRESH_TOKEN_COOKIE_PATH,
                                remainingTtl(refreshHeader)).toString());
            }

            return response;
        };
    }

    private BiFunction<ServerRequest, ServerResponse, ServerResponse> clearAuthCookies() {
        return (request, response) -> {
            if (!response.statusCode().is2xxSuccessful()) {
                return response;
            }

            response.headers().add(HttpHeaders.SET_COOKIE,
                    clearCookie(JwtConfiguration.TokenType.accessToken(), ACCESS_TOKEN_COOKIE_PATH).toString());
            response.headers().add(HttpHeaders.SET_COOKIE,
                    clearCookie(JwtConfiguration.TokenType.refreshToken(), REFRESH_TOKEN_COOKIE_PATH).toString());

            return response;
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
        return ServerResponse.status(HttpStatus.UNAUTHORIZED).body(new GatewayError("INVALID_TOKEN", "Invalid token"));
    }

    private Duration remainingTtl(String token) {
        Duration d = Duration.between(Instant.now(), jwtService.extractExpiration(token));
        return d.isNegative() ? Duration.ZERO : d;
    }
}
