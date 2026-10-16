package com.earmarked.earmark.controller;

import com.earmarked.earmark.config.ApiPaths;
import com.earmarked.earmark.dto.CreateEarmarkRequest;
import com.earmarked.earmark.dto.EarmarkResponse;
import com.earmarked.earmark.service.EarmarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping(path = ApiPaths.BASE_API_URL)
@RestController
public class EarmarkController {
    private final EarmarkService earmarkService;

    @PostMapping
    public ResponseEntity<EarmarkResponse> create(@AuthenticationPrincipal UUID userId, @Valid @RequestBody CreateEarmarkRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(earmarkService.create(userId, request));
    }
}
