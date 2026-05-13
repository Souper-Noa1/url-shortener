package com.url.shortener.exception;

public class UrlNotFoundException extends AppException {

    public UrlNotFoundException(String shortCode) {
        super("URL_NOT_FOUND", "No URL found for short code: " + shortCode);
    }
}
