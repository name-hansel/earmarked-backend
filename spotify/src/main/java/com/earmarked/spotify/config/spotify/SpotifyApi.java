package com.earmarked.spotify.config.spotify;

import com.earmarked.spotify.dto.spotify.SpotifySearchResponse;
import com.earmarked.spotify.dto.spotify.album.SpotifyAlbumFull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "spotify-api", url = "${spotify.api-url}", configuration = SpotifyFeignConfig.class)
public interface SpotifyApi {

    @GetMapping("/search")
    SpotifySearchResponse search(@RequestParam("q") String query, @RequestParam("type") String type, @RequestParam("limit") int limit);

    @GetMapping("/albums/{id}")
    SpotifyAlbumFull getAlbum(@PathVariable("id") String id);
}
