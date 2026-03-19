package com.parth.urlshortener.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "urls")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Url {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	
	@Column (nullable = false, unique = true, length=10)
	private String shortCode;
	
	@Column(nullable = false, columnDefinition = "TEXT")
	private String originalUrl;
	
	@Column(updatable = false)
	private LocalDateTime createdAt;
	
	private LocalDateTime expiresAt;
	
	private Long userId;
	
	@Builder.Default
	private Boolean isActive = true;
	
	@Builder.Default
	private Long clickCount = 0L;
	
	@PrePersist
	protected void onCreate(){
		createdAt = LocalDateTime.now();
	}
	
}
