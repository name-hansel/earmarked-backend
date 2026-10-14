package com.earmarked.spotify.dto.spotify;

import java.util.List;

public record TrackDetails(String id, String name, Integer trackNumber, Long durationMs, List<String> artists) {
}
