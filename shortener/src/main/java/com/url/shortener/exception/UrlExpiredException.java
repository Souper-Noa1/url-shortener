package com.url.shortener.exception;

public class UrlExpiredException extends AppException {
    public UrlExpiredException(String shortCode) {
        super("URL_EXPIRED", "The URL for short code '" + shortCode + "' has expired");
    }
}
