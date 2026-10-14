package com.earmarked.spotify.controller;

import com.earmarked.spotify.config.ApiPaths;
import com.earmarked.spotify.dto.AlbumDetails;
import com.earmarked.spotify.dto.AlbumSummary;
import com.earmarked.spotify.service.SpotifyService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = ApiPaths.BASE_API_URL)
@RequiredArgsConstructor
@Validated
public class SpotifyController {

    private final SpotifyService spotifyService;

    @GetMapping(ApiPaths.SEARCH)
    public List<AlbumSummary> search(@RequestParam("q") @NotBlank String query) {
        return spotifyService.searchAlbums(query);
    }

    @GetMapping(ApiPaths.ALBUM)
    public AlbumDetails getAlbum(@PathVariable @Pattern(regexp = "^[A-Za-z0-9]{22}$", message = "Invalid album id") String id) {
        return spotifyService.getAlbum(id);
    }
}
