package com.earmarked.spotify.dto;

import java.util.List;

public record AlbumDetails(String id, String name, List<String> artists, List<AlbumImage> images, String releaseDate,
                           Integer totalTracks, String albumType, List<TrackDetails> tracks) {

    public AlbumSummary toSummary() {
        return new AlbumSummary(id, name, artists, images, releaseDate, albumType);
    }
}
