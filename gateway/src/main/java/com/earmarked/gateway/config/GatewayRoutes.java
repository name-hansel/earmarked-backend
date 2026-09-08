package com.earmarked.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.function.Function;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class GatewayRoutes {

    @Bean
    public RouterFunction<ServerResponse> authServiceRoute() {
        return route("auth-service").route(path("/api/auth/**"), http()).before(translateCookiesToHeaders())
                .before(uri("http://localhost:8081")).build();
    }

    private Function<ServerRequest, ServerRequest> translateCookiesToHeaders() {
        return (request) -> {
            var accessTokenCookie = request.cookies().getFirst(JwtConfiguration.TokenType.accessToken());
            var refreshTokenCookie = request.cookies().getFirst(JwtConfiguration.TokenType.refreshToken());

            ServerRequest.Builder builder = ServerRequest.from(request)
                    .headers(headers -> headers.remove(HttpHeaders.COOKIE));

            if (accessTokenCookie != null) {
                builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenCookie.getValue());
            }

            // TODO: Scope only to /refresh and /logout?
            if (refreshTokenCookie != null) {
                builder.header("X-Refresh-Token", refreshTokenCookie.getValue());
            }

            return builder.build();
        };
    }
}
