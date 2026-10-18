package com.earmarked.earmark.config.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "spotify-service", configuration = SpotifyClientConfig.class)
public interface SpotifyClient {

    @GetMapping("/api/spotify/albums/{id}")
    void ensureValidAlbumId(@PathVariable("id") String id);

}
