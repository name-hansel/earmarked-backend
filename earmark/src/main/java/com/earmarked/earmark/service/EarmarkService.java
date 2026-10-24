package com.earmarked.earmark.service;

import com.earmarked.earmark.config.EarmarkStatus;
import com.earmarked.earmark.config.client.SpotifyClient;
import com.earmarked.earmark.dto.AlbumEarmarkResponse;
import com.earmarked.earmark.dto.CreateEarmarkRequest;
import com.earmarked.earmark.dto.EarmarkResponse;
import com.earmarked.earmark.dto.UpdateEarmarkRequest;
import com.earmarked.earmark.dto.spotify.AlbumSummary;
import com.earmarked.earmark.entity.Earmark;
import com.earmarked.earmark.exception.EarmarkAlreadyExistsException;
import com.earmarked.earmark.exception.EarmarkNotFoundException;
import com.earmarked.earmark.exception.InvalidListenedDateException;
import com.earmarked.earmark.exception.InvalidListenedStateException;
import com.earmarked.earmark.repository.EarmarkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EarmarkService {
    private final SpotifyClient spotifyClient;
    private final EarmarkWriter writer;
    private final EarmarkRepository repository;

    public EarmarkResponse create(UUID userId, CreateEarmarkRequest request) {
        AlbumSummary album = spotifyClient.getAlbum(request.spotifyAlbumId());

        try {
            Earmark saved = writer.save(userId, request, album.name(), toReleaseDate(album), getAlbumArtists(album));
            return EarmarkResponse.from(saved);
        } catch (DataIntegrityViolationException e) {
            if (isAlreadyEarmarked(e)) {
                throw new EarmarkAlreadyExistsException();
            }
            throw e;
        }
    }

    public Page<EarmarkResponse> getAll(UUID userId, String q, Pageable pageable, EarmarkStatus status, Boolean hasNote) {
        Boolean listened = status == null ? null : status == EarmarkStatus.LISTENED;
        Page<Earmark> earmarks = (q == null || q.isBlank())
                ? repository.findFiltered(userId, listened, hasNote, pageable)
                : repository.search(userId, toLikePattern(q), listened, hasNote, pageable);

        Page<EarmarkResponse> page = earmarks.map(EarmarkResponse::from);
        Map<String, AlbumSummary> albumMetadata = fetchAlbumMetadata(page.getContent());

        return page.map(earmark -> earmark.withAlbum(albumMetadata.get(earmark.spotifyAlbumId())));
    }

    @Transactional
    public EarmarkResponse update(UUID userId, UUID id, UpdateEarmarkRequest request) {
        Earmark earmark = repository.findByIdAndUserId(id, userId).orElseThrow(EarmarkNotFoundException::new);

        if (!request.listened() && request.listenedAt() != null) {
            throw new InvalidListenedStateException();
        }

        validateListenedAt(request.listenedAt());

        String note = request.note() == null || request.note().isBlank() ? null : request.note().trim();
        earmark.setNote(note);
        earmark.setListened(request.listened());
        earmark.setListenedAt(request.listenedAt());

        return EarmarkResponse.from(earmark);
    }

    @Transactional
    public void delete(UUID id, UUID userId) {
        if (repository.deleteByIdAndUserId(id, userId) == 0) {
            throw new EarmarkNotFoundException();
        }
    }

    @Transactional(readOnly = true)
    public AlbumEarmarkResponse findForAlbum(UUID userId, String spotifyAlbumId) {
        Optional<Earmark> earmark = repository.findByUserIdAndSpotifyAlbumId(userId, spotifyAlbumId);

        return new AlbumEarmarkResponse(earmark.map(EarmarkResponse::from).orElse(null));
    }

    @Transactional
    public EarmarkResponse updateListened(UUID userId, UUID id, boolean listened) {
        Earmark earmark = repository.findByIdAndUserId(id, userId).orElseThrow(EarmarkNotFoundException::new);

        earmark.setListened(listened);
        if (!listened) {
            earmark.setListenedAt(null);
        }

        return EarmarkResponse.from(earmark);
    }

    private boolean isAlreadyEarmarked(DataIntegrityViolationException e) {
        Throwable cause = e.getMostSpecificCause();
        return cause instanceof SQLException sql
                && "23505".equals(sql.getSQLState())
                && cause.getMessage() != null
                && cause.getMessage().contains(Earmark.UQ_USER_ALBUM);
    }

    private Map<String, AlbumSummary> fetchAlbumMetadata(List<EarmarkResponse> earmarks) {
        List<String> spotifyAlbumIds = earmarks.stream().map(EarmarkResponse::spotifyAlbumId).distinct().toList();

        if (spotifyAlbumIds.isEmpty()) {
            return Map.of();
        }

        try {
            return spotifyClient.getAlbums(spotifyAlbumIds);
        } catch (Exception e) {
            log.warn("Could not load album metadata, returning earmarks without it", e);
            return Map.of();
        }
    }

    private static LocalDate toReleaseDate(AlbumSummary album) {
        String d = album.releaseDate();
        return switch (album.releaseDatePrecision()) {
            case "year" -> Year.parse(d).atDay(1);
            case "month" -> YearMonth.parse(d).atDay(1);
            case "day" -> LocalDate.parse(d);
            default -> throw new IllegalStateException("Unknown precision: " + album.releaseDatePrecision());
        };
    }

    private static String getAlbumArtists(AlbumSummary album) {
        String albumArtists = String.join(", ", album.artists());

        return albumArtists.length() > 1024 ? albumArtists.substring(0, 1024) : albumArtists;
    }

    private static String toLikePattern(String q) {
        String escaped = q.trim().toLowerCase()
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        return "%" + escaped + "%";
    }

    private void validateListenedAt(LocalDate listenedAt) {
        if (listenedAt != null && listenedAt.isAfter(LocalDate.now(ZoneOffset.UTC).plusDays(1))) {
            throw new InvalidListenedDateException();
        }
    }
}
