package com.earmarked.earmark.dto;

import jakarta.validation.constraints.Size;

public record UpdateEarmarkRequest(@Size(max = 256) String note) {
}
