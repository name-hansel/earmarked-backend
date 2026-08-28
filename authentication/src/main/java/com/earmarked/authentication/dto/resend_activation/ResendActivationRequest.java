package com.earmarked.authentication.dto.resend_activation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendActivationRequest(@NotBlank @Email String email) {
}
