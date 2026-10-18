package com.earmarked.earmark.service;

import com.earmarked.earmark.config.client.SpotifyClient;
import com.earmarked.earmark.dto.CreateEarmarkRequest;
import com.earmarked.earmark.dto.EarmarkResponse;
import com.earmarked.earmark.entity.Earmark;
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
    private final SpotifyClient spotifyClient;
    private final EarmarkWriter writer;
    private final EarmarkRepository repository;

    public EarmarkResponse create(UUID userId, CreateEarmarkRequest request) {
        spotifyClient.ensureValidAlbumId(request.spotifyAlbumId());
        Earmark saved = writer.save(userId, request);

        return EarmarkResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<EarmarkResponse> getAll(UUID userId, Pageable pageable) {
        return repository.findByUserId(userId, pageable).map(EarmarkResponse::from);
    }
}
