package com.earmarked.authentication.dto;

import java.util.UUID;

public record UserDTO(UUID userId, String username, String email) {
}
