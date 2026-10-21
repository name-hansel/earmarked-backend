package com.earmarked.spotify.dto.spotify.album;

import com.earmarked.spotify.dto.spotify.SpotifyArtist;
import com.earmarked.spotify.dto.spotify.SpotifyImage;
import com.earmarked.spotify.dto.spotify.track.SpotifyTracks;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyAlbumFull(String id, String name,
                               List<SpotifyArtist> artists,
                               List<SpotifyImage> images,
                               @JsonProperty("release_date") String releaseDate,
                               @JsonProperty("total_tracks") Integer totalTracks,
                               @JsonProperty("album_type") String albumType,
                               SpotifyTracks tracks,
                               @JsonProperty("external_urls") Map<String, String> externalUrls) implements SpotifyAlbumData {
}

