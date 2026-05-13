package com.url.shortener.exception;

public class UrlAccessDeniedException extends AppException {
    public UrlAccessDeniedException() {
        super("ACCESS_DENIED", "You do not have permission to modify this URL");
    }
}
