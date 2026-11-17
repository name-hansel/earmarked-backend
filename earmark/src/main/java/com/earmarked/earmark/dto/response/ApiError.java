package com.earmarked.earmark.dto.response;

import java.util.Map;

public record ApiError(String code, String message, Map<String, String> fieldErrors) {
}
