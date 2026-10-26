package com.earmarked.authentication.dto;

import java.util.UUID;

public record UserDeletedEvent(UUID userId) {
}
