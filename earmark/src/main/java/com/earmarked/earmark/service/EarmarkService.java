package com.earmarked.earmark.service;

import com.earmarked.earmark.config.client.SpotifyClient;
import com.earmarked.earmark.dto.AlbumEarmarkResponse;
import com.earmarked.earmark.dto.CreateEarmarkRequest;
import com.earmarked.earmark.dto.EarmarkResponse;
import com.earmarked.earmark.entity.Earmark;
import com.earmarked.earmark.exception.EarmarkAlreadyExistsException;
import com.earmarked.earmark.exception.EarmarkNotFoundException;
import com.earmarked.earmark.repository.EarmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EarmarkService {
    private final SpotifyClient spotifyClient;
    private final EarmarkWriter writer;
    private final EarmarkRepository repository;

    public EarmarkResponse create(UUID userId, CreateEarmarkRequest request) {
        spotifyClient.ensureValidAlbumId(request.spotifyAlbumId());

        try {
            Earmark saved = writer.save(userId, request);
            return EarmarkResponse.from(saved);
        } catch (DataIntegrityViolationException e) {
            if (isAlreadyEarmarked(e)) {
                throw new EarmarkAlreadyExistsException();
            }
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Page<EarmarkResponse> getAll(UUID userId, Pageable pageable) {
        return repository.findByUserId(userId, pageable).map(EarmarkResponse::from);
    }

    @Transactional
    public void delete(UUID id, UUID userId) {
        if (repository.deleteByIdAndUserId(id, userId) == 0) {
            throw new EarmarkNotFoundException();
        }
    }

    public AlbumEarmarkResponse findForAlbum(UUID userId, String spotifyAlbumId) {
        Optional<Earmark> earmark = repository.findByUserIdAndSpotifyAlbumId(userId, spotifyAlbumId);

        return new AlbumEarmarkResponse(earmark.map(EarmarkResponse::from).orElse(null));
    }

    private boolean isAlreadyEarmarked(DataIntegrityViolationException e) {
        Throwable cause = e.getMostSpecificCause();
        return cause instanceof SQLException sql
                && "23505".equals(sql.getSQLState())
                && cause.getMessage() != null
                && cause.getMessage().contains(Earmark.UQ_USER_ALBUM);
    }
}
