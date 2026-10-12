package com.earmarked.spotify.config;

public final class ApiPaths {

    public final static String BASE_API_URL = "/api/spotify";

    public static String getPath(String suffix) {
        return ApiPaths.BASE_API_URL + suffix;
    }

    private ApiPaths() {
    }
}
