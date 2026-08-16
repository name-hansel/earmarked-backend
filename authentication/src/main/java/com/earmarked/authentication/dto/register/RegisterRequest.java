package com.earmarked.authentication.dto.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank
                              @Email
                              @Size(max = 320)
                              String email,

                              @NotBlank
                              @Size(min = 8, max = 128)
                              String password,

                              @NotBlank
                              @Size(min = 3, max = 16)
                              @Pattern(regexp = "^[a-zA-Z0-9._-]+$")
                              String username) {
}
