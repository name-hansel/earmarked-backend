package com.earmarked.spotify.controller;

import com.earmarked.spotify.config.ApiPaths;
import com.earmarked.spotify.dto.Album;
import com.earmarked.spotify.service.SpotifyService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = ApiPaths.BASE_API_URL)
@RequiredArgsConstructor
@Validated
public class SpotifyController {

    private final SpotifyService spotifyService;

    @GetMapping(ApiPaths.SEARCH)
    public List<Album> search(@RequestParam("q") @NotBlank String query) {
        return spotifyService.searchAlbums(query);
    }
}
