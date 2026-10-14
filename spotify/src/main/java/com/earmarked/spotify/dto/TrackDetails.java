package com.earmarked.spotify.dto;

import java.util.List;

public record TrackDetails(String id, String name, Integer trackNumber, Long durationMs, List<String> artists) {
}
