package com.earmarked.earmark.config;

import lombok.Getter;
import org.springframework.data.domain.Sort;

@Getter
public enum EarmarkSortOption {
    EARMARKED_DESC(Sort.by(Sort.Order.desc("createdAt"))),
    EARMARKED_ASC(Sort.by(Sort.Order.asc("createdAt"))),
    ALBUM_NAME_ASC(Sort.by(Sort.Order.asc("albumName").ignoreCase())),
    ALBUM_NAME_DESC(Sort.by(Sort.Order.desc("albumName").ignoreCase())),
    RELEASE_DATE_DESC(Sort.by(Sort.Order.desc("albumReleaseDate"))),
    RELEASE_DATE_ASC(Sort.by(Sort.Order.asc("albumReleaseDate")));

    private final Sort sort;

    EarmarkSortOption(Sort sort) {
        // id for tie-breaker
        this.sort = sort.and(Sort.by("id"));
    }

}
