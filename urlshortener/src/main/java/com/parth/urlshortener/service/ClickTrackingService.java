package com.parth.urlshortener.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.parth.urlshortener.entity.ClickAnalytics;
import com.parth.urlshortener.entity.Url;
import com.parth.urlshortener.repository.ClickAnalyticsRepository;
import com.parth.urlshortener.repository.UrlRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClickTrackingService {

    private final UrlRepository urlRepository;
    private final ClickAnalyticsRepository analyticsRepo;

    /**
     * Async click tracking — does NOT block the redirect response.
     * 
     * IMPORTANT: We accept plain Strings instead of HttpServletRequest because
     * @Async runs in a separate thread. By that time the original request is
     * already completed and recycled by Tomcat, making req.getRemoteAddr() etc.
     * return null or throw errors.
     */
    @Async
    @Transactional
    public void trackClickAsync(String shortCode, String ipAddress, String userAgent, String referer) {
        try {
            Url url = urlRepository.findByShortCode(shortCode).orElse(null);
            if (url == null)
                return;

            urlRepository.incrementClickCount(shortCode);

            analyticsRepo.save(ClickAnalytics.builder()
                    .urlId(url.getId())
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .referer(referer)
                    .build());

            log.info("Click tracked for shortCode: {}", shortCode);
        } catch (Exception e) {
            log.error("Error tracking click for {}: {}", shortCode, e.getMessage());
        }
    }
}
