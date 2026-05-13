package com.url.shortener.domain.dtos.response;

import java.time.Instant;


public record UrlStatsResponse(
        String shortCode,
        String originalUrl,
        Long clickCount,
        Instant createdAt,
        Instant expiresAt,
        boolean isExpired
) {}

