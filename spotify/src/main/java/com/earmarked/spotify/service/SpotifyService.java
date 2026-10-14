package com.earmarked.spotify.service;

import com.earmarked.spotify.config.CacheConfig;
import com.earmarked.spotify.dto.AlbumDetails;
import com.earmarked.spotify.dto.AlbumSummary;
import com.earmarked.spotify.dto.spotify.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpotifyService {

    private final SpotifyClient spotifyClient;

    @Cacheable(value = CacheConfig.ALBUM_SEARCH, key = "#query.trim().toLowerCase()")
    public List<AlbumSummary> searchAlbums(String query) {
        SpotifySearchResponse searchResponse = spotifyClient.searchAlbums(query);

        if (searchResponse == null || searchResponse.albums() == null || searchResponse.albums().items() == null) {
            return List.of();
        }

        return searchResponse.albums().items().stream().filter(Objects::nonNull).map(this::toSummary).toList();
    }

    @Cacheable(value = CacheConfig.ALBUMS, key = "#id")
    public AlbumDetails getAlbum(String id) {
        return toDetails(spotifyClient.getAlbum(id));
    }

    private AlbumSummary toSummary(SpotifyAlbum album) {
        return new AlbumSummary(album.id(), album.name(), getArtists(album),
                CollectionUtils.isEmpty(album.images()) ? null : album.images().getLast().url(), album.releaseDate(),
                album.albumType());
    }

    private AlbumDetails toDetails(SpotifyAlbum album) {
        List<TrackDetails> tracks = (album.tracks() == null || album.tracks().items() == null)
                ? List.of()
                : album.tracks().items().stream()
                .filter(Objects::nonNull)
                .map(this::getTrackDetails)
                .toList();

        return new AlbumDetails(album.id(), album.name(), getArtists(album),
                CollectionUtils.isEmpty(album.images()) ? null : album.images().getFirst().url(), album.releaseDate(),
                album.totalTracks(), album.albumType(), tracks);
    }

    private TrackDetails getTrackDetails(SpotifyTrack track) {
        return new TrackDetails(track.id(), track.name(), track.trackNumber(), track.durationMs(), getArtists(track));
    }

    private List<String> getArtists(SpotifyTrack track) {
        return Optional.ofNullable(track.artists())
                .map((spotifyArtists) -> spotifyArtists.stream().map(SpotifyArtist::name).toList()).orElse(List.of());
    }

    private List<String> getArtists(SpotifyAlbum album) {
        return Optional.ofNullable(album.artists())
                .map((spotifyArtists) -> spotifyArtists.stream().map(SpotifyArtist::name).toList()).orElse(List.of());
    }
}
