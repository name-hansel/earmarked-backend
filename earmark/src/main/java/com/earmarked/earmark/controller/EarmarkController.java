package com.earmarked.earmark.controller;

import com.earmarked.earmark.config.ApiPaths;
import com.earmarked.earmark.config.EarmarkSortOption;
import com.earmarked.earmark.config.EarmarkStatus;
import com.earmarked.earmark.dto.*;
import com.earmarked.earmark.service.EarmarkService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@Validated
@RequestMapping(path = ApiPaths.BASE_API_URL)
@RestController
public class EarmarkController {
    private final EarmarkService earmarkService;

    @PostMapping
    public ResponseEntity<EarmarkResponse> create(@AuthenticationPrincipal UUID userId, @Valid @RequestBody CreateEarmarkRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(earmarkService.create(userId, request));
    }

    @GetMapping
    public Page<EarmarkResponse> getAll(@AuthenticationPrincipal UUID userId, @RequestParam(defaultValue = "0") @Min(0) int page,
                                        @RequestParam(defaultValue = "20") @Min(1) @Max(50) int size,
                                        @RequestParam(defaultValue = "EARMARKED_DESC") EarmarkSortOption sort,
                                        @RequestParam(required = false) @Size(max = 100) String q,
                                        @RequestParam(required = false) EarmarkStatus status,
                                        @RequestParam(required = false) Boolean hasNote) {
        Pageable pageable = PageRequest.of(page, size, sort.getSort());
        return earmarkService.getAll(userId, q, pageable, status, hasNote);
    }

    @PatchMapping("/{id}")
    public EarmarkResponse update(@AuthenticationPrincipal UUID userId, @PathVariable UUID id, @Valid @RequestBody UpdateEarmarkRequest updateEarmarkRequest) {
        return earmarkService.update(userId, id, updateEarmarkRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UUID userId, @PathVariable UUID id) {
        earmarkService.delete(id, userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/album/{albumId}")
    public AlbumEarmarkResponse findForAlbum(@AuthenticationPrincipal UUID userId, @PathVariable String albumId) {
        return earmarkService.findForAlbum(userId, albumId);
    }

    @PatchMapping("/{id}/listened")
    public EarmarkResponse updateListened(@AuthenticationPrincipal UUID userId, @PathVariable UUID id, @RequestBody UpdateListenedRequest request) {
        return earmarkService.updateListened(userId, id, request.listened());
    }
}
