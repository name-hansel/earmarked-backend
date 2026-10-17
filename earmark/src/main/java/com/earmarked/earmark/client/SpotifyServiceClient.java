package com.earmarked.earmark.client;

import com.earmarked.earmark.dto.spotify.AlbumSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "spotify")
public interface SpotifyServiceClient {

    @GetMapping("/api/spotify/albums/{id}")
    AlbumSummary getAlbum(@PathVariable("id") String id);

}
