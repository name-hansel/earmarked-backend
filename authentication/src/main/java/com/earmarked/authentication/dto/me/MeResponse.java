package com.earmarked.authentication.dto.me;

import java.util.UUID;

public record MeResponse(UUID userId, String username) {
}
