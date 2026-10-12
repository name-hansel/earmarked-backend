package com.earmarked.spotify.dto;

import java.util.List;

public record Album(String id, String name, List<String> artists, String imageUrl, String releaseDate) {
}
