package com.earmarked.spotify.config.spotify;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;

import java.net.http.HttpClient;
import java.time.Duration;

public final class SpotifyHttp {
    private SpotifyHttp() {

    }

    public static ClientHttpRequestFactory requestFactory() {
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();

        JdkClientHttpRequestFactory httpRequestFactory = new JdkClientHttpRequestFactory(httpClient);
        httpRequestFactory.setReadTimeout(Duration.ofSeconds(5));

        return httpRequestFactory;
    }
}
