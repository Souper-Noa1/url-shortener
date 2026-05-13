package com.url.shortener.domain.dtos.response;

import com.url.shortener.domain.entities.UrlStatus;

import java.time.Instant;


public record UrlResponse(
        Long id,
        String originalUrl,
        String shortCode,
        String shortUrl,
        UrlStatus status,
        Long clickCount,
        Instant expiresAt,
        Instant createdAt
) {}

