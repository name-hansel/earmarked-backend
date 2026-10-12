package com.earmarked.spotify.service;

import com.earmarked.spotify.dto.Album;
import com.earmarked.spotify.dto.spotify.SpotifyAlbum;
import com.earmarked.spotify.dto.spotify.SpotifyArtist;
import com.earmarked.spotify.dto.spotify.SpotifySearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpotifyService {

    private final SpotifyClient spotifyClient;

    public List<Album> searchAlbums(String query) {
        SpotifySearchResponse searchResponse = spotifyClient.searchAlbums(query);

        if (searchResponse == null || searchResponse.albums() == null || searchResponse.albums().items() == null) {
            return List.of();
        }

        return searchResponse.albums().items().stream().filter(Objects::nonNull).map(this::toAlbum).toList();
    }

    public Album getAlbum(String id) {
        return toAlbum(spotifyClient.getAlbum(id));
    }

    private Album toAlbum(SpotifyAlbum album) {
        List<String> artists = Optional.ofNullable(album.artists())
                .map((spotifyArtists) -> spotifyArtists.stream().map(SpotifyArtist::name).toList())
                .orElse(List.of());
        String imageUrl = CollectionUtils.isEmpty(album.images()) ? null : album.images().getFirst().url();

        return new Album(album.id(), album.name(), artists, imageUrl, album.releaseDate());
    }
}
