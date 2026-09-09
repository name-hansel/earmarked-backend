package com.earmarked.gateway.config;

public class ApiPaths {
    public static final String AUTH_BASE = "/api/auth";
    public static final String LOGIN = AUTH_BASE + "/login";
    public static final String REFRESH = AUTH_BASE + "/refresh";
    public static final String LOGOUT = AUTH_BASE + "/logout";
    public static final String DELETE_ACCOUNT = AUTH_BASE + "/delete-account";
    public static final String AUTH_WILDCARD = AUTH_BASE + "/**";
}
