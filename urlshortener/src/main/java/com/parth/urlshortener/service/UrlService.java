package com.parth.urlshortener.service;

import java.util.List;

import com.parth.urlshortener.dto.ShortenRequest;
import com.parth.urlshortener.dto.ShortenResponse;
import com.parth.urlshortener.dto.UrlStatsResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface UrlService {

    ShortenResponse shortenUrl(ShortenRequest request, Long userId);
    String resolveUrl(String shortCode, HttpServletRequest request);
    UrlStatsResponse getUrlStats(String shortCode);
    void deactivateUrl(String shortCode);
    List<ShortenResponse> getUserUrls(Long userId);
}
