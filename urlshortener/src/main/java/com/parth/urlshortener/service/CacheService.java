package com.parth.urlshortener.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String CACHE_PREFIX = "url:";
    private static final long DEFAULT_TTL_HOURS = 24;

    public void cacheUrl(String shortCode, String originalUrl) {
        try {
            redisTemplate.opsForValue().set(
                    CACHE_PREFIX + shortCode,
                    originalUrl,
                    DEFAULT_TTL_HOURS,
                    TimeUnit.HOURS
            );
        } catch (Exception e) {
            log.warn("Redis cache write failed for {}: {}", shortCode, e.getMessage());
            // Gracefully degrade — app works without cache, just slower
        }
    }

    public String getCachedUrl(String shortCode) {
        try {
            return redisTemplate.opsForValue().get(CACHE_PREFIX + shortCode);
        } catch (Exception e) {
            log.warn("Redis cache read failed for {}: {}", shortCode, e.getMessage());
            return null; // Fall back to DB lookup
        }
    }

    public void evictCache(String shortCode) {
        try {
            redisTemplate.delete(CACHE_PREFIX + shortCode);
        } catch (Exception e) {
            log.warn("Redis cache evict failed for {}: {}", shortCode, e.getMessage());
        }
    }
}
