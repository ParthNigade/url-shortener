package com.parth.urlshortener.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.parth.urlshortener.entity.Url;

import java.time.LocalDateTime;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long>{

	Optional<Url> findByShortCode (String shortcode);
	boolean existsByShortCode(String shortCode);
	List <Url> findByUserIdOrderByCreatedAtDesc(Long userId);
	List<Url> findByIsActiveTrueAndExpiresAtBefore(LocalDateTime now);

	@Modifying
	@Query("UPDATE Url u SET u.clickCount = u.clickCount + 1 WHERE u.shortCode = :code")
	void incrementClickCount(@Param("code") String shortCode);
	}
