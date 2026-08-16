package com.earmarked.authentication.config;

public final class ApiPaths {

    public static final String AUTH = "/api/auth";
    public static final String REGISTER = "/register";
    public static final String ACTIVATE_ACCOUNT = "/activate-account";
    public static final String LOGIN = AUTH + "/login";

    public static String getPath(String suffix) {
        return ApiPaths.AUTH + suffix;
    }

    private ApiPaths() {
    }
}
