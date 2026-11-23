package com.earmarked.earmark.repository;

import com.earmarked.earmark.entity.EarmarkEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EarmarkRepository extends JpaRepository<EarmarkEntity, UUID> {
    boolean existsByUserIdAndSpotifyAlbumId(UUID userId, String spotifyAlbumId);

    long deleteByIdAndUserId(UUID id, UUID userId);

    Optional<EarmarkEntity> findByUserIdAndSpotifyAlbumId(UUID userId, String spotifyAlbumId);

    Optional<EarmarkEntity> findByIdAndUserId(UUID id, UUID userId);

    @Query("""
            SELECT e FROM EarmarkEntity e
            WHERE e.userId = :userId
              AND (lower(e.albumName) LIKE :pattern ESCAPE '\\'
                OR lower(e.albumArtists) LIKE :pattern ESCAPE '\\'
                OR lower(e.note) LIKE :pattern ESCAPE '\\')
              AND (:listened IS NULL OR e.listened = :listened)
              AND (:hasNote IS NULL
                OR (:hasNote = TRUE AND e.note IS NOT NULL AND e.note <> '')
                OR (:hasNote = FALSE AND (e.note IS NULL or e.note = '')))
              AND (:hasReview IS NULL
                OR (:hasReview = TRUE AND e.review IS NOT NULL AND e.review <> '')
                OR (:hasReview = FALSE AND (e.review IS NULL or e.review = '')))
              AND (:ratingValue IS NULL OR e.rating = :ratingValue)
              AND (:unrated = FALSE OR e.rating IS NULL)
            """)
    Page<EarmarkEntity> search(@Param("userId") UUID userId, @Param("pattern") String pattern, @Param("listened") Boolean listened, @Param("hasNote") Boolean hasNote, @Param("ratingValue") Short ratingValue, @Param("unrated") boolean unrated, @Param("hasReview") Boolean hasReview, Pageable pageable);

    @Query("""
            SELECT e FROM EarmarkEntity e
            WHERE e.userId = :userId
              AND (:listened IS null OR e.listened = :listened)
              AND (:hasNote IS NULL
                   OR (:hasNote = TRUE AND e.note IS NOT NULL AND e.note <> '')
                   OR (:hasNote = FALSE AND (e.note IS NULL OR e.note = '')))
              AND (:hasReview IS NULL
                OR (:hasReview = TRUE AND e.review IS NOT NULL AND e.review <> '')
                OR (:hasReview = FALSE AND (e.review IS NULL or e.review = '')))
              AND (:ratingValue IS NULL OR e.rating = :ratingValue)
              AND (:unrated = FALSE OR e.rating IS NULL)
            """)
    Page<EarmarkEntity> findFiltered(@Param("userId") UUID userId, @Param("listened") Boolean listened, @Param("hasNote") Boolean hasNote, @Param("ratingValue") Short ratingValue, @Param("unrated") boolean unrated, @Param("hasReview") Boolean hasReview, Pageable pageable);

    List<EarmarkEntity> findByUserIdAndSpotifyAlbumIdIn(UUID userId, List<String> spotifyAlbumIds);

    @Modifying
    @Query("delete from EarmarkEntity e where e.userId = :userId")
    int deleteAllByUserId(@Param("userId") UUID userId);
}
