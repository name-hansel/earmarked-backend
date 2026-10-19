package com.earmarked.earmark.config.client;

import com.earmarked.earmark.dto.spotify.AlbumSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "spotify-service", configuration = SpotifyClientConfig.class)
public interface SpotifyClient {

    @GetMapping("/api/spotify/albums/{id}")
    void ensureValidAlbumId(@PathVariable("id") String id);

    @GetMapping("/api/spotify/albums")
    Map<String, AlbumSummary> getAlbums(@RequestParam("ids") List<String> ids);
}
