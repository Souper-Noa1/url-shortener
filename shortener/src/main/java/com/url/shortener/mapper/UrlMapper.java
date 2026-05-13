package com.url.shortener.mapper;

import com.url.shortener.domain.dtos.response.UrlResponse;
import com.url.shortener.domain.dtos.response.UrlStatsResponse;
import com.url.shortener.domain.entities.Url;
import org.springframework.stereotype.Component;


@Component
public class UrlMapper {


    public UrlResponse toResponse(Url url, String baseUrl) {
        return new UrlResponse(
                url.getId(),
                url.getOriginalUrl(),
                url.getShortCode(),
                baseUrl + "/r/" + url.getShortCode(),   // computed, not stored
                url.getStatus(),
                url.getClickCount(),
                url.getExpiresAt(),
                url.getCreatedAt()
        );
    }


    public UrlStatsResponse toStatsResponse(Url url) {
        return new UrlStatsResponse(
                url.getShortCode(),
                url.getOriginalUrl(),
                url.getClickCount(),
                url.getCreatedAt(),
                url.getExpiresAt(),
                url.isExpired()
        );
    }
}

