package com.parth.urlshortener.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UrlStatsResponse {
	
	private String shortCode;
	private String originalUrl;
	private Long totalClicks;
	private LocalDateTime createdAt;
	private LocalDateTime expiresAt;
	private Boolean isActive;
}
