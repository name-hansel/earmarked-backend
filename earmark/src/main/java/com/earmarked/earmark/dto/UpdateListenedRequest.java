package com.earmarked.earmark.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateListenedRequest(@NotNull Boolean listened) {
}
