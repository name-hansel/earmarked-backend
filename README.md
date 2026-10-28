# earmarked – Backend

## Overview

Java (Spring Boot) microservices backend for earmarked, an app for earmarking albums to listen to. Users search Spotify,
earmark albums (optionally with a note), mark them as listened, and sort, search and filter their list. All traffic from
the browser goes through a gateway that handles cookie-based JWT auth.

## Architecture

| Service                | Responsibility                                                                                                                    |
|------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| Gateway (8080)         | Single entry point. Translates HttpOnly cookies to `Authorization` / refresh headers and back, CORS, routing via Eureka (`lb://`) |
| Auth service (8081)    | Registration, activation, login, refresh, logout, password flows, account deletion                                                |
| Spotify service (8082) | Album search, album details, batch lookup. Spotify token handling and caching (Redis)                                             |
| Earmark service (8083) | User-owned earmarks: CRUD, sorting, search, filters, listened state                                                               |
| Eureka server (8761)   | Service discovery                                                                                                                 |

Services call each other with OpenFeign. Account deletion is propagated asynchronously: the auth service publishes a
`user.deleted` event to Kafka and the earmark service deletes that user's earmarks.

## Tech Stack

- Java 21, Spring Boot 4.1, Maven
- Spring Cloud: Gateway Server MVC, Eureka, OpenFeign
- PostgreSQL (per service) with Flyway migrations
- Redis (token and album caching)
- Kafka (KRaft, single node)
- JWT (RS256) with jjwt
- Docker Compose for infrastructure (services run locally)

## Features

- Registration with email activation, login, logout, token refresh
- Forgot / reset / change password, delete account
- Spotify album search and detail pages (batch metadata lookup for lists)
- Earmark albums with an optional note; one earmark per user per album
- Listened state with an optional listened date
- Server-side sorting with pagination
- Weekly cleanup of expired refresh tokens

## Authentication Flow

- The browser only ever holds HttpOnly, Secure, SameSite=Strict cookies (`accessToken`, `refreshToken`)
- The gateway validates the access cookie, removes the `Cookie` header and forwards the token as
  `Authorization: Bearer`, plus the refresh token in `X-Refresh-Token`
- On login and refresh, the gateway converts the auth service's response headers into `Set-Cookie`
- JWTs are signed with an RSA key held by the auth service. Other services only have the public key
- Two-layer validation: the gateway does a fail-fast check, and every service re-verifies the JWT itself
- Refresh tokens are stored and revoked on logout, password change/reset and account deletion
- Logout is idempotent and works even with an expired cookie
- CSRF: SameSite=Strict cookies, password required for sensitive actions, and the gateway's CORS config rejects
  non-allowed origins

## Future Improvements

- Deployment
- JWKS endpoint and key rotation instead of a static public key
- Tags for earmarks
- Leave ratings and reviews for an earmark after listening
- Social feed for earmark-related activities

## Attribution

Album data and artwork are provided by Spotify.