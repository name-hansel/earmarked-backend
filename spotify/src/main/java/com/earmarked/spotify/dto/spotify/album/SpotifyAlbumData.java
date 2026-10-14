package com.earmarked.spotify.dto.spotify.album;

import com.earmarked.spotify.dto.spotify.SpotifyArtist;
import com.earmarked.spotify.dto.spotify.SpotifyImage;

import java.util.List;

public interface SpotifyAlbumData {
    String id();

    String name();

    List<SpotifyArtist> artists();

    List<SpotifyImage> images();

    String releaseDate();

    String albumType();
}
