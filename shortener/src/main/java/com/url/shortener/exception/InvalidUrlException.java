package com.url.shortener.exception;

public class InvalidUrlException extends AppException {
    public InvalidUrlException(String errorCode, String message) {
        super(errorCode, message);
    }
}
