package com.earmarked.earmark.service;

import com.earmarked.earmark.dto.CreateEarmarkRequest;
import com.earmarked.earmark.entity.Earmark;
import com.earmarked.earmark.exception.EarmarkAlreadyExistsException;
import com.earmarked.earmark.repository.EarmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EarmarkWriter {

    private final EarmarkRepository repository;

    @Transactional
    public Earmark save(UUID userId, CreateEarmarkRequest request) {
        if (repository.existsByUserIdAndSpotifyAlbumId(userId, request.spotifyAlbumId())) {
            throw new EarmarkAlreadyExistsException();
        }

        Earmark earmark = new Earmark();
        earmark.setUserId(userId);
        earmark.setSpotifyAlbumId(request.spotifyAlbumId());
        earmark.setNote(request.note());
        
        return repository.save(earmark);
    }
}
