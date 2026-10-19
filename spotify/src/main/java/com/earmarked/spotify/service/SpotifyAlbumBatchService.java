package com.earmarked.spotify.service;

import com.earmarked.spotify.dto.AlbumSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpotifyAlbumBatchService {
    private final SpotifyService spotifyService;
    private final ExecutorService albumExecutor;

    public Map<String, AlbumSummary> getAlbums(List<String> spotifyAlbumIds) {
        LinkedHashMap<String, CompletableFuture<AlbumSummary>> futures = spotifyAlbumIds.stream().distinct()
                .collect(Collectors.toMap(id -> id,
                        id -> CompletableFuture.supplyAsync(() -> spotifyService.getAlbum(id).toSummary(),
                                        albumExecutor)
                                .exceptionally(e -> {
                                    log.warn("Could not load album {}", id, e);
                                    return null;
                                }),
                        (a, b) -> a, LinkedHashMap::new));

        CompletableFuture.allOf(futures.values().toArray(CompletableFuture[]::new)).join();

        Map<String, AlbumSummary> result = new LinkedHashMap<>();
        futures.forEach((id, future) -> {
            AlbumSummary albumSummary = future.join();
            if (albumSummary != null) {
                result.put(id, albumSummary);
            }
        });

        return result;
    }
}
