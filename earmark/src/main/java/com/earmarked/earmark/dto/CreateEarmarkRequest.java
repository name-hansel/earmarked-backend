package com.earmarked.earmark.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateEarmarkRequest(@NotBlank @Size(max = 32) String spotifyAlbumId, @Size(max = 256) String note) {
}
