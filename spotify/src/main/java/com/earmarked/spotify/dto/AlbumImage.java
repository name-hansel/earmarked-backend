package com.earmarked.spotify.dto;

import com.earmarked.spotify.dto.spotify.SpotifyImage;

public record AlbumImage(String url, Integer width, Integer height) {

    public static AlbumImage fromSpotifyImage(SpotifyImage image) {
        return new AlbumImage(image.url(), image.width(), image.height());
    }
}
