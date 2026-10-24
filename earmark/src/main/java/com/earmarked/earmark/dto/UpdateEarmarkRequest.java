package com.earmarked.earmark.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateEarmarkRequest(@Size(max = 256) String note, LocalDate listenedAt) {
}
