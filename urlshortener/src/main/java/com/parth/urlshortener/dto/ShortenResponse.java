package com.parth.urlshortener.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortenResponse {
	
	private String shortCode;
	private String shortUrl;
	private String originalUrl;
	private LocalDateTime createdAt;
	private LocalDateTime expiresAt;
	
}
