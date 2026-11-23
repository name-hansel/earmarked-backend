package com.earmarked.earmark.service;

import com.earmarked.earmark.dto.request.CreateEarmarkRequest;
import com.earmarked.earmark.entity.EarmarkEntity;
import com.earmarked.earmark.exception.EarmarkAlreadyExistsException;
import com.earmarked.earmark.repository.EarmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EarmarkWriter {

    private final EarmarkRepository repository;

    @Transactional
    EarmarkEntity save(UUID userId, CreateEarmarkRequest request, String albumName, LocalDate albumReleaseDate, String albumArtists) {
        if (repository.existsByUserIdAndSpotifyAlbumId(userId, request.spotifyAlbumId())) {
            throw new EarmarkAlreadyExistsException();
        }

        EarmarkEntity earmarkEntity = new EarmarkEntity();
        earmarkEntity.setUserId(userId);
        earmarkEntity.setSpotifyAlbumId(request.spotifyAlbumId());
        earmarkEntity.setNote(request.note());
        
        earmarkEntity.setAlbumName(albumName);
        earmarkEntity.setAlbumReleaseDate(albumReleaseDate);
        earmarkEntity.setAlbumArtists(albumArtists);

        return repository.save(earmarkEntity);
    }
}
