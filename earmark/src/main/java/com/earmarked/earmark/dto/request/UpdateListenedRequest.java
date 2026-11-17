package com.earmarked.earmark.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateListenedRequest(@NotNull Boolean listened) {
}
