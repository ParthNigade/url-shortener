package com.parth.urlshortener.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parth.urlshortener.dto.ShortenRequest;
import com.parth.urlshortener.dto.ShortenResponse;
import com.parth.urlshortener.dto.UrlStatsResponse;
import com.parth.urlshortener.entity.User;
import com.parth.urlshortener.service.UrlService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shorten(
            @Valid @RequestBody ShortenRequest req,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(urlService.shortenUrl(req, user.getId()));
    }

    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<UrlStatsResponse> getStats(@PathVariable String shortCode) {
        return ResponseEntity.ok(urlService.getUrlStats(shortCode));
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deactivate(@PathVariable String shortCode) {
        urlService.deactivateUrl(shortCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-urls")
    public ResponseEntity<List<ShortenResponse>> getMyUrls(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(urlService.getUserUrls(user.getId()));
    }
}
