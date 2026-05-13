package com.url.shortener.domain.dtos.response;

import java.time.Instant;

public record ErrorResponse(
        String errorCode,
        String message,
        Instant timestamp,
        String path
) {

    public static ErrorResponse of(String errorCode, String message, String path) {
        return new ErrorResponse(errorCode, message, Instant.now(), path);
    }
}
