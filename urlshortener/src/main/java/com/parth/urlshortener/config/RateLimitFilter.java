package com.parth.urlshortener.config;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Stores timestamps of requests per IP
    private final ConcurrentHashMap<String, List<Long>> requestCounts = new ConcurrentHashMap<>();

    private static final int MAX_REQUESTS = 10;         // max requests allowed
    private static final long TIME_WINDOW_MS = 60_000;  // 1 minute window

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                     FilterChain chain) throws ServletException, IOException {
        // Only rate-limit the shorten endpoint
        if (req.getRequestURI().startsWith("/api/v1/url/shorten")) {
            String clientIp = req.getRemoteAddr();
            long now = System.currentTimeMillis();

            requestCounts.putIfAbsent(clientIp, new CopyOnWriteArrayList<>());
            List<Long> timestamps = requestCounts.get(clientIp);

            // Remove timestamps older than the time window
            timestamps.removeIf(t -> now - t > TIME_WINDOW_MS);

            if (timestamps.size() < MAX_REQUESTS) {
                timestamps.add(now);
                chain.doFilter(req, res);
            } else {
                res.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                res.setContentType("application/json");
                res.getWriter().write("{\"error\":\"Rate limit exceeded. Try again later.\"}");
            }
        } else {
            chain.doFilter(req, res);
        }
    }
}

