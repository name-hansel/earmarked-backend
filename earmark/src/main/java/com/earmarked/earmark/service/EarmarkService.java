package com.earmarked.earmark.service;

import com.earmarked.earmark.EarmarkRepository;
import com.earmarked.earmark.dto.CreateEarmarkRequest;
import com.earmarked.earmark.dto.EarmarkResponse;
import com.earmarked.earmark.entity.Earmark;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EarmarkService {
    private final EarmarkRepository repository;

    public EarmarkResponse create(UUID userId, CreateEarmarkRequest request) {
        Earmark earmark = new Earmark();
        earmark.setUserId(userId);
        earmark.setSpotifyAlbumId(request.spotifyAlbumId());
        earmark.setNote(request.note());

        return EarmarkResponse.from(repository.save(earmark));
    }

}
