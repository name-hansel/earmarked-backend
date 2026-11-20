package com.earmarked.earmark.config.enums;

import lombok.Getter;

@Getter
public enum EarmarkRating {
    ZERO(0),
    ONE(1),
    TWO(2),
    THREE(3),
    NO_RATING(null);

    private final Short value;

    EarmarkRating(Integer value) {
        this.value = value == null ? null : value.shortValue();
    }
}
