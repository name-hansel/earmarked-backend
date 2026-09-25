package com.earmarked.gateway.config;

import com.earmarked.gateway.config.jwt.JwtConfiguration;
import com.earmarked.gateway.config.jwt.JwtService;
import com.earmarked.gateway.config.services.ServiceBaseUrlProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.server.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.time.Duration;
import java.time.Instant;
import java.util.function.BiFunction;
import java.util.function.Function;

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
        return route("auth-login-refresh")
                .route(path(ApiPaths.LOGIN).or(path(ApiPaths.REFRESH)), http())
                .before(translateCookiesToHeaders())
                .before(uri(serviceBaseUrlProperties.auth()))
                .after(setAuthCookies())
                .build()
                .and(route("auth-logout-delete-account").route(path(ApiPaths.LOGOUT).or(path(ApiPaths.DELETE_ACCOUNT)),
                                http())
                        .before(translateCookiesToHeaders())
                        .before(uri(serviceBaseUrlProperties.auth()))
                        .after(clearAuthCookies())
                        .build())
                .and(route("auth-generic").route(path(ApiPaths.AUTH_WILDCARD), http())
                        .before(translateCookiesToHeaders())
                        .before(uri(serviceBaseUrlProperties.auth()))
                        .build());
    }

    private Function<ServerRequest, ServerRequest> translateCookiesToHeaders() {
        return (request) -> {
            var accessTokenCookie = request.cookies().getFirst(JwtConfiguration.TokenType.accessToken());
            var refreshTokenCookie = request.cookies().getFirst(JwtConfiguration.TokenType.refreshToken());

            ServerRequest.Builder builder = ServerRequest.from(request)
                    .headers(headers -> headers.remove(HttpHeaders.COOKIE));

            if (accessTokenCookie != null) {
                builder.header(HttpHeaders.AUTHORIZATION,
                        JwtConfiguration.BEARER_PREFIX + accessTokenCookie.getValue());
            }

            if (refreshTokenCookie != null) {
                builder.header(JwtConfiguration.REFRESH_TOKEN_HEADER, refreshTokenCookie.getValue());
            }

            return builder.build();
        };
    }

    private BiFunction<ServerRequest, ServerResponse, ServerResponse> setAuthCookies() {
        return (request, response) -> {
            if (!response.statusCode().is2xxSuccessful()) {
                return response;
            }

            String authHeader = response.headers().getFirst(HttpHeaders.AUTHORIZATION);
            String refreshHeader = response.headers().getFirst(JwtConfiguration.REFRESH_TOKEN_HEADER);

            ServerResponse.BodyBuilder builder = ServerResponse.from(response);

            if (authHeader != null && authHeader.startsWith(JwtConfiguration.BEARER_PREFIX)) {
                String accessToken = authHeader.substring(JwtConfiguration.BEARER_PREFIX.length());
                Duration maxAge = Duration.between(Instant.now(), jwtService.extractExpiration(accessToken));
                builder.header(HttpHeaders.SET_COOKIE,
                        buildCookie(JwtConfiguration.TokenType.accessToken(), accessToken,
                                ACCESS_TOKEN_COOKIE_PATH,
                                maxAge).toString());
            }

            if (refreshHeader != null) {
                Duration maxAge = Duration.between(Instant.now(), jwtService.extractExpiration(refreshHeader));
                builder.header(HttpHeaders.SET_COOKIE,
                        buildCookie(JwtConfiguration.TokenType.refreshToken(), refreshHeader, REFRESH_TOKEN_COOKIE_PATH,
                                maxAge).toString());
            }

            return builder.build();
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
}
