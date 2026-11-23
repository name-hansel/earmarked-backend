package com.earmarked.earmark.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateEarmarkRequest(@Size(max = 256) String note, @NotNull Boolean listened, LocalDate listenedAt,
                                   @Min(0) @Max(3) Short rating, @Size(max = 256) String review) {
}
