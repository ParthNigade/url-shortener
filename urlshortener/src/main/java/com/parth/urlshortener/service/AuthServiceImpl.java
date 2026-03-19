package com.parth.urlshortener.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.parth.urlshortener.dto.AuthRequest;
import com.parth.urlshortener.dto.AuthResponse;
import com.parth.urlshortener.entity.User;
import com.parth.urlshortener.repository.UserRepository;
import com.parth.urlshortener.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(AuthRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken!");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }

        // Create and save user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .message("Registration successful!")
                .build();
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        // Find user
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password!"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid username or password!");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .message("Login successful!")
                .build();
    }
}
