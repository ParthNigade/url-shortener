package com.parth.urlshortener.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.parth.urlshortener.entity.Url;
import com.parth.urlshortener.repository.UrlRepository;
import com.parth.urlshortener.service.CacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UrlCleanupScheduler {

    private final UrlRepository urlRepository;
    private final CacheService cacheService;

    /**
     * Runs every hour to deactivate expired URLs and evict them from Redis cache.
     */
    @Scheduled(fixedRate = 3600000) // every 1 hour
    @Transactional
    public void cleanupExpiredUrls() {
        List<Url> expiredUrls = urlRepository.findByIsActiveTrueAndExpiresAtBefore(LocalDateTime.now());

        if (expiredUrls.isEmpty()) {
            return;
        }

        log.info("Found {} expired URLs to deactivate", expiredUrls.size());

        for (Url url : expiredUrls) {
            url.setIsActive(false);
            urlRepository.save(url);
            cacheService.evictCache(url.getShortCode());
            log.info("Deactivated expired URL: {}", url.getShortCode());
        }
    }
}
