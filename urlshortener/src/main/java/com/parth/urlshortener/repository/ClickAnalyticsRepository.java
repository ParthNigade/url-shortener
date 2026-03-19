package com.parth.urlshortener.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.parth.urlshortener.entity.ClickAnalytics;

@Repository
public interface ClickAnalyticsRepository extends JpaRepository<ClickAnalytics, Long> {
	List<ClickAnalytics> findByUrlIdOrderByClickedAtDesc(Long urlId);
	long countByUrlId(Long urlId);
}
