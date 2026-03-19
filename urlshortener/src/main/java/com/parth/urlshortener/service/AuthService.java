package com.parth.urlshortener.service;

import com.parth.urlshortener.dto.AuthRequest;
import com.parth.urlshortener.dto.AuthResponse;

public interface AuthService {
    AuthResponse register(AuthRequest request);
    AuthResponse login(AuthRequest request);
}
