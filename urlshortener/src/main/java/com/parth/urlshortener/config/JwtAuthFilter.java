package com.parth.urlshortener.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.parth.urlshortener.entity.User;
import com.parth.urlshortener.repository.UserRepository;
import com.parth.urlshortener.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // No token → skip
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            final String username = jwtUtil.extractUsername(token);

            // Only set auth if not already authenticated
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findByUsername(username).orElse(null);

                if (user != null && jwtUtil.isTokenValid(token, username)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Invalid token — just continue without auth
        }

        filterChain.doFilter(request, response);
    }
}
