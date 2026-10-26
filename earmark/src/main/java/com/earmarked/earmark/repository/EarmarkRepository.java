package com.earmarked.earmark.repository;

import com.earmarked.earmark.entity.Earmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EarmarkRepository extends JpaRepository<Earmark, UUID> {
    boolean existsByUserIdAndSpotifyAlbumId(UUID userId, String spotifyAlbumId);

    long deleteByIdAndUserId(UUID id, UUID userId);

    Optional<Earmark> findByUserIdAndSpotifyAlbumId(UUID userId, String spotifyAlbumId);

    Optional<Earmark> findByIdAndUserId(UUID id, UUID userId);

    @Query("""
            SELECT e FROM Earmark e
            WHERE e.userId = :userId
              AND (lower(e.albumName) LIKE :pattern ESCAPE '\\'
                OR lower(e.albumArtists) LIKE :pattern ESCAPE '\\'
                OR lower(e.note) LIKE :pattern ESCAPE '\\')
              AND (:listened IS NULL
                OR (:listened = TRUE AND e.listenedAt IS NOT NULL)
                OR (:listened = FALSE AND e.listenedAt IS NULL))
              AND (:hasNote IS NULL
                OR (:hasNote = TRUE AND e.note IS NOT NULL AND e.note <> '')
                OR (:hasNote = FALSE AND (e.note IS NULL or e.note = '')))
            """)
    Page<Earmark> search(@Param("userId") UUID userId,
                         @Param("pattern") String pattern,
                         @Param("listened") Boolean listened,
                         @Param("hasNote") Boolean hasNote,
                         Pageable pageable);

    @Query("""
            SELECT e FROM Earmark e
            WHERE e.userId = :userId
              AND (:listened IS null OR e.listened = :listened)
              AND (:hasNote IS NULL
                   OR (:hasNote = TRUE AND e.note IS NOT NULL AND e.note <> '')
                   OR (:hasNote = FALSE AND (e.note IS NULL OR e.note = '')))
            """)
    Page<Earmark> findFiltered(@Param("userId") UUID userId,
                               @Param("listened") Boolean listened,
                               @Param("hasNote") Boolean hasNote,
                               Pageable pageable);

    List<Earmark> findByUserIdAndSpotifyAlbumIdIn(UUID userId, List<String> spotifyAlbumIds);

    @Modifying
    @Query("delete from Earmark e where e.userId = :userId")
    int deleteAllByUserId(@Param("userId") UUID userId);
}
