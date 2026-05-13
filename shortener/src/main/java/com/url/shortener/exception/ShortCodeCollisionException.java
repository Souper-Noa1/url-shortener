package com.url.shortener.exception;

public class ShortCodeCollisionException extends AppException {
    public ShortCodeCollisionException(String shortCode) {
        super("SHORT_CODE_COLLISION", "Short code already exists: " + shortCode);
    }
}