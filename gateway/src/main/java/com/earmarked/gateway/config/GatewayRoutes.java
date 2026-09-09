package com.earmarked.gateway.config;

import com.earmarked.gateway.config.services.ServiceBaseUrlProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.function.BiFunction;
import java.util.function.Function;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class GatewayRoutes {
    private ServiceBaseUrlProperties serviceBaseUrlProperties;

    @Bean
    public RouterFunction<ServerResponse> authServiceRoute() {
        return route("auth-login-refresh")
                .route(path(ApiPaths.LOGIN).or(path(ApiPaths.REFRESH)), http())
                .before(translateCookiesToHeaders())
                .before(uri(serviceBaseUrlProperties.auth()))
                .after(setAuthCookies())
                .build()
                .and(route("auth-logout-delete-account")
                        .route(path(ApiPaths.LOGOUT).or(path(ApiPaths.DELETE_ACCOUNT)), http())
                        .before(translateCookiesToHeaders())
                        .before(uri(serviceBaseUrlProperties.auth()))
                        .after(clearAuthCookies())
                        .build())
                .and(route("auth-generic")
                        .route(path(ApiPaths.AUTH_WILDCARD), http())
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
            String authHeader = response.headers().getFirst(HttpHeaders.AUTHORIZATION);
            String refreshHeader = response.headers().getFirst(JwtConfiguration.REFRESH_TOKEN_HEADER);

            ServerResponse.BodyBuilder builder = ServerResponse.from(response);

            if (authHeader != null && authHeader.startsWith(JwtConfiguration.BEARER_PREFIX)) {
                String accessToken = authHeader.substring(JwtConfiguration.BEARER_PREFIX.length());
                // TODO: Set cookie
            }

            if (refreshHeader != null) {
                // TODO: Set cookie
            }

            return builder.build();
        };
    }

    private BiFunction<ServerRequest, ServerResponse, ServerResponse> clearAuthCookies() {
        return (request, response) -> {
            ServerResponse.BodyBuilder builder = ServerResponse.from(response);

            // TODO: Clear cookies

            return builder.build();
        };
    }
}
