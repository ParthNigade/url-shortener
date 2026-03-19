package com.parth.urlshortener.service;

import com.parth.urlshortener.dto.AnalyticsResponse;

public interface AnalyticsService {
    AnalyticsResponse getDetailedAnalytics(String shortCode);

}
