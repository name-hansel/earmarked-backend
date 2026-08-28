package com.earmarked.authentication.dto.delete_account;

import jakarta.validation.constraints.NotBlank;

public record DeleteAccountRequest(@NotBlank String password) {
}
