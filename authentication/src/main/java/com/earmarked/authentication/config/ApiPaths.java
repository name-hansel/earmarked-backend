package com.earmarked.authentication.config;

public final class ApiPaths {

    public static final String AUTH = "/api/auth";
    public static final String REGISTER = "/register";
    public static final String ACTIVATE_ACCOUNT = "/activate-account";
    public static final String LOGIN = "/login";
    public static final String ME = "/me";
    public static final String LOGOUT = "/logout";
    public static final String REFRESH = "/refresh";
    public static final String DELETE_ACCOUNT = "/delete-account";
    public static final String RESEND_ACTIVATION = "/resend-activation";
    public static final String CHANGE_PASSWORD = "/change-password";
    public static final String FORGOT_PASSWORD = "/forgot-password";

    public static String getPath(String suffix) {
        return ApiPaths.AUTH + suffix;
    }

    private ApiPaths() {
    }
}
