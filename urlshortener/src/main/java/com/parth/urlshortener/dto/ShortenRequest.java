package com.parth.urlshortener.dto;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShortenRequest {
	
	@NotBlank(message = "URL is required")
	@URL(message = "Invalid URL format")
	private String originalUrl;
	
	private Integer expiresInDays;
	
	private String customAlias; //optional
}
