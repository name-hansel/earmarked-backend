package com.earmarked.spotify.dto.spotify.album;

import com.earmarked.spotify.dto.spotify.SpotifyArtist;
import com.earmarked.spotify.dto.spotify.SpotifyImage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyAlbum(String id, String name, List<SpotifyArtist> artists,
                           List<SpotifyImage> images, @JsonProperty("release_date") String releaseDate,
                           @JsonProperty("release_date_precision") String releaseDatePrecision,
                           @JsonProperty("album_type") String albumType,
                           @JsonProperty("external_urls") Map<String, String> externalUrls) implements SpotifyAlbumData {
}
