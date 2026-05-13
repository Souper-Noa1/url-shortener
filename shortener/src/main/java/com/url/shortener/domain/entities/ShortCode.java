package com.url.shortener.domain.entities;

import com.url.shortener.exception.InvalidUrlException;

import java.util.Objects;
import java.util.regex.Pattern;


public final class ShortCode {

    // 6 alphanumeric characters: a-z, A-Z, 0-9
    private static final Pattern VALID_PATTERN = Pattern.compile("^[a-zA-Z0-9]{6}$");

    private final String value;

    /**
     * Factory method — validates before constructing.
     * Throws InvalidUrlException (our custom exception) on bad input.
     */
    public ShortCode(String value) {
        if (value == null || !VALID_PATTERN.matcher(value).matches()) {
            throw new InvalidUrlException(
                    "SHORT_CODE_INVALID",
                    "Short code must be exactly 6 alphanumeric characters, got: " + value
            );
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShortCode other)) return false;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}

