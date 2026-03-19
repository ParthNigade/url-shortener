package com.parth.urlshortener.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.parth.urlshortener.dto.AnalyticsResponse;
import com.parth.urlshortener.entity.ClickAnalytics;
import com.parth.urlshortener.entity.Url;
import com.parth.urlshortener.exception.UrlNotFoundException;
import com.parth.urlshortener.repository.ClickAnalyticsRepository;
import com.parth.urlshortener.repository.UrlRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

	private final UrlRepository urlRepository;
    private final ClickAnalyticsRepository clickAnalyticsRepository;

    /**
     * Returns detailed click analytics for a given short code.
     * Includes total clicks and the last 50 recent click events.
     */
    @Override
    public AnalyticsResponse getDetailedAnalytics(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        List<ClickAnalytics> clicks = clickAnalyticsRepository
                .findByUrlIdOrderByClickedAtDesc(url.getId());

        List<AnalyticsResponse.ClickDetail> recentClicks = clicks.stream()
                .limit(50)
                .map(click -> AnalyticsResponse.ClickDetail.builder()
                        .clickedAt(click.getClickedAt())
                        .ipAddress(click.getIpAddress())
                        .userAgent(click.getUserAgent())
                        .referer(click.getReferer())
                        .build())
                .collect(Collectors.toList());

        return AnalyticsResponse.builder()
                .shortCode(shortCode)
                .totalClicks(url.getClickCount())
                .recentClicks(recentClicks)
                .build();
    }
}
