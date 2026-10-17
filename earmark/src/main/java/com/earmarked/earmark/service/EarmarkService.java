package com.earmarked.earmark.service;

import com.earmarked.earmark.client.SpotifyServiceClient;
import com.earmarked.earmark.dto.CreateEarmarkRequest;
import com.earmarked.earmark.dto.EarmarkResponse;
import com.earmarked.earmark.entity.Earmark;
import com.earmarked.earmark.exception.EarmarkAlreadyExistsException;
import com.earmarked.earmark.repository.EarmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EarmarkService {
    private final EarmarkRepository repository;
    private final SpotifyServiceClient spotifyServiceClient;

    @Transactional
    public EarmarkResponse create(UUID userId, CreateEarmarkRequest request) {
        String spotifyAlbumId = request.spotifyAlbumId();
        spotifyServiceClient.getAlbum(spotifyAlbumId);

        if (repository.existsByUserIdAndSpotifyAlbumId(userId, spotifyAlbumId)) {
            throw new EarmarkAlreadyExistsException();
        }

        Earmark earmark = new Earmark();
        earmark.setUserId(userId);
        earmark.setSpotifyAlbumId(spotifyAlbumId);
        earmark.setNote(request.note());

        return EarmarkResponse.from(repository.save(earmark));
    }

    @Transactional(readOnly = true)
    public Page<EarmarkResponse> getAll(UUID userId, Pageable pageable) {
        return repository.findByUserId(userId, pageable).map(EarmarkResponse::from);
    }
}
