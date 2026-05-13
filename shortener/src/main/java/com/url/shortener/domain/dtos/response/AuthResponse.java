package com.url.shortener.domain.dtos.response;

public record AuthResponse(
        String token,
        String tokenType,
        String username,
        long expiresIn
) {}
