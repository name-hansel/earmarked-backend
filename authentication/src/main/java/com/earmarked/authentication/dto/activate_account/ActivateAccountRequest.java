package com.earmarked.authentication.dto.activate_account;

import jakarta.validation.constraints.NotBlank;

public record ActivateAccountRequest(@NotBlank String token) {
}
