package com.parth.urlshortener.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parth.urlshortener.dto.AnalyticsResponse;
import com.parth.urlshortener.service.AnalyticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
	
	private final AnalyticsService analyticsService;

    @GetMapping("/{shortCode}")
    public ResponseEntity<AnalyticsResponse> getDetailedAnalytics(
            @PathVariable String shortCode) {
        return ResponseEntity.ok(analyticsService.getDetailedAnalytics(shortCode));
    }
}
