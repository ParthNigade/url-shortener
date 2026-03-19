package com.parth.urlshortener.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.parth.urlshortener.service.UrlService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RedirectController {

	private final UrlService urlService;

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode, HttpServletRequest request) {
        String originalUrl = urlService.resolveUrl(shortCode, request);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, originalUrl)
                .build();
    }

}
