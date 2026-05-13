package com.url.shortener.domain.dtos.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.Instant;

public record CreateUrlRequest(

        @NotBlank(message = "Original URL must not be blank")
        @URL(message = "Must be a valid URL (include http:// or https://)")
        @Size(max = 2048, message = "URL must not exceed 2048 characters")
        String originalUrl,

        @Future(message = "Expiry date must be in the future")
        Instant expiresAt

) {}
