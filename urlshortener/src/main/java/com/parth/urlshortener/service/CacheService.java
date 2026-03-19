package com.parth.urlshortener.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CacheService {

	private final RedisTemplate<String, String> redisTemplate;
	private static final String CACHE_PREFIX = "url:";
	private static final long DEFAULT_TTL_HOURS = 24;
	
	public void cacheUrl(String shortCode, String originalUrl) {
		redisTemplate.opsForValue().set(
				CACHE_PREFIX + shortCode,
				originalUrl,
				DEFAULT_TTL_HOURS,
				TimeUnit.HOURS
				);
	}
	
	public String getCachedUrl(String shortCode) {
		return redisTemplate.opsForValue().get(CACHE_PREFIX + shortCode);
	}
	
	public void evictCache(String shortCode) {
		redisTemplate.delete(CACHE_PREFIX + shortCode);
	}
	
}
