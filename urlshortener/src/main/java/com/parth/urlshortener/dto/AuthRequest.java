package com.parth.urlshortener.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String email;      // used only for registration
    private String password;
}
