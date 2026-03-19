package com.parth.urlshortener.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.parth.urlshortener.dto.ShortenRequest;
import com.parth.urlshortener.dto.ShortenResponse;
import com.parth.urlshortener.dto.UrlStatsResponse;
import com.parth.urlshortener.entity.Url;
import com.parth.urlshortener.exception.UrlExpiredException;
import com.parth.urlshortener.exception.UrlNotFoundException;
import com.parth.urlshortener.repository.UrlRepository;
import com.parth.urlshortener.util.Base62Util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final CacheService cacheService;
    private final Base62Util base62Util;
    private final ClickTrackingService clickTrackingService;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.short-code-length}")
    private int codeLength;

    @Value("${app.default-expiry-days}")
    private int defaultExpiryDays;

    @Override
    @Transactional
    public ShortenResponse shortenUrl(ShortenRequest request, Long userId) {
        String shortCode;

        // Check if user wants a custom alias
        if (request.getCustomAlias() != null && !request.getCustomAlias().isBlank()) {
            shortCode = request.getCustomAlias();
            if (urlRepository.existsByShortCode(shortCode)) {
                throw new RuntimeException("Custom alias already taken!");
            }
        } else {
            shortCode = generateUniqueCode();
        }

        int expiry = request.getExpiresInDays() != null
                ? request.getExpiresInDays() : defaultExpiryDays;

        Url url = Url.builder()
                .shortCode(shortCode)
                .originalUrl(request.getOriginalUrl())
                .expiresAt(LocalDateTime.now().plusDays(expiry))
                .userId(userId)
                .build();

        urlRepository.save(url);

        // Cache the URL in Redis for fast lookups
        cacheService.cacheUrl(shortCode, url.getOriginalUrl());

        return ShortenResponse.builder()
                .shortCode(shortCode)
                .shortUrl(baseUrl + "/" + shortCode)
                .originalUrl(url.getOriginalUrl())
                .createdAt(url.getCreatedAt())
                .expiresAt(url.getExpiresAt())
                .build();
    }

    @Override
    public String resolveUrl(String shortCode, HttpServletRequest request) {
        // Extract request data NOW (on the main thread) before async call
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");

        // STEP 1: Check Redis cache first (fast path)
        String cached = cacheService.getCachedUrl(shortCode);
        if (cached != null) {
            log.info("Cache HIT for shortCode: {}", shortCode);
            clickTrackingService.trackClickAsync(shortCode, ipAddress, userAgent, referer);
            return cached;
        }

        // STEP 2: Cache miss — fall back to MySQL
        log.info("Cache MISS for shortCode: {}, querying DB", shortCode);
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        // Check if URL is still active
        if (!url.getIsActive()) {
            throw new UrlExpiredException(shortCode);
        }

        // Check if URL has expired
        if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())) {
            url.setIsActive(false);
            urlRepository.save(url);
            cacheService.evictCache(shortCode);
            throw new UrlExpiredException(shortCode);
        }

        // STEP 3: Populate cache for future lookups & track click
        cacheService.cacheUrl(shortCode, url.getOriginalUrl());
        clickTrackingService.trackClickAsync(shortCode, ipAddress, userAgent, referer);

        return url.getOriginalUrl();
    }

    @Override
    public UrlStatsResponse getUrlStats(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        return UrlStatsResponse.builder()
                .shortCode(url.getShortCode())
                .originalUrl(url.getOriginalUrl())
                .totalClicks(url.getClickCount())
                .createdAt(url.getCreatedAt())
                .expiresAt(url.getExpiresAt())
                .isActive(url.getIsActive())
                .build();
    }

    @Override
    @Transactional
    public void deactivateUrl(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));
        url.setIsActive(false);
        urlRepository.save(url);
        cacheService.evictCache(shortCode);
    }

    @Override
    public List<ShortenResponse> getUserUrls(Long userId) {
        return urlRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(url -> ShortenResponse.builder()
                        .shortCode(url.getShortCode())
                        .shortUrl(baseUrl + "/" + url.getShortCode())
                        .originalUrl(url.getOriginalUrl())
                        .createdAt(url.getCreatedAt())
                        .expiresAt(url.getExpiresAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Collision-safe code generation.
     */
    private String generateUniqueCode() {
        String code;
        int attempts = 0;
        do {
            code = base62Util.generateRandomCode(codeLength);
            attempts++;
            if (attempts > 10) {
                throw new RuntimeException("Failed to generate unique short code after 10 attempts");
            }
        } while (urlRepository.existsByShortCode(code));
        return code;
    }
}
