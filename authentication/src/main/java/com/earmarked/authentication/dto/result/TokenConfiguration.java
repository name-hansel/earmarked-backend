package com.earmarked.authentication.dto.result;

import java.time.Duration;

public record TokenConfiguration(String keyPrefixUser, String keyPrefixToken, Duration tokenTtl) {
}
