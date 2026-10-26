package com.earmarked.spotify.controller;

import com.earmarked.spotify.config.ApiPaths;
import com.earmarked.spotify.dto.AlbumDetails;
import com.earmarked.spotify.dto.AlbumSummary;
import com.earmarked.spotify.service.SpotifyAlbumBatchService;
import com.earmarked.spotify.service.SpotifyService;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = ApiPaths.BASE_API_URL)
@RequiredArgsConstructor
@Validated
public class SpotifyController {

    private final SpotifyService spotifyService;
    private final SpotifyAlbumBatchService spotifyAlbumBatchService;

    @GetMapping(ApiPaths.SEARCH)
    public List<AlbumSummary> search(@RequestParam("q") @NotBlank String query, @RequestParam(defaultValue = "6") @Min(1) @Max(10) int size) {
        return spotifyService.searchAlbums(query, size);
    }

    @GetMapping(ApiPaths.ALBUM)
    public AlbumDetails getAlbum(@PathVariable @Pattern(regexp = "^[A-Za-z0-9]{22}$", message = "Invalid album id") String id) {
        return spotifyService.getAlbum(id);
    }

    @GetMapping
    public Map<String, AlbumSummary> getAlbums(@RequestParam @Size(min = 1, max = 20) List<String> ids) {
        return spotifyAlbumBatchService.getAlbums(ids);
    }
}
