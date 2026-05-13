package com.url.shortener.service;

import com.url.shortener.config.AppConfig;
import com.url.shortener.domain.dtos.request.CreateUrlRequest;
import com.url.shortener.domain.dtos.response.UrlResponse;
import com.url.shortener.domain.dtos.response.UrlStatsResponse;
import com.url.shortener.domain.entities.Url;
import com.url.shortener.domain.entities.UrlStatus;
import com.url.shortener.domain.entities.User;
import com.url.shortener.exception.ShortCodeCollisionException;
import com.url.shortener.exception.UrlAccessDeniedException;
import com.url.shortener.exception.UrlExpiredException;
import com.url.shortener.exception.UrlNotFoundException;
import com.url.shortener.mapper.UrlMapper;
import com.url.shortener.repository.UrlRepository;

import com.url.shortener.utils.ShortCodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;


@Service
public class UrlService {

    private static final Logger log = LoggerFactory.getLogger(UrlService.class);
    private static final int MAX_COLLISION_RETRIES = 3;

    private final UrlRepository urlRepository;
    private final ShortCodeGenerator codeGenerator;
    private final AppConfig appConfig;
    private final UrlMapper urlMapper;


    public UrlService(UrlRepository urlRepository,
                      ShortCodeGenerator codeGenerator,
                      AppConfig appConfig,
                      UrlMapper urlMapper) {
        this.urlRepository = urlRepository;
        this.codeGenerator = codeGenerator;
        this.appConfig = appConfig;
        this.urlMapper = urlMapper;
    }


    @Transactional
    public UrlResponse createShortUrl(CreateUrlRequest request, User owner) {
        String shortCode = generateUniqueCode();

        // Resolve expiry: use request value, or fall back to default from config
        Instant expiresAt = request.expiresAt() != null
                ? request.expiresAt()
                : Instant.now().plus(appConfig.getDefaultExpiryDays(), ChronoUnit.DAYS);

        Url url = new Url(request.originalUrl(), shortCode, expiresAt, owner);
        Url saved = urlRepository.save(url);

        log.info("Created short URL: {} -> {} (owner: {})", shortCode, request.originalUrl(), owner.getUsername());

        return urlMapper.toResponse(saved, appConfig.getBaseUrl());
    }


    @Transactional
    public String resolveUrl(String shortCode) {
        Url url = findActiveUrl(shortCode);
        url.incrementClickCount();
        // No explicit save() needed — JPA's dirty checking detects the change
        // and flushes an UPDATE at transaction commit time.

        log.debug("Redirecting short code {} (click #{})", shortCode, url.getClickCount());
        return url.getOriginalUrl();
    }


    @Transactional(readOnly = true)
    public UrlResponse getUrlDetails(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));
        return urlMapper.toResponse(url, appConfig.getBaseUrl());
    }


    @Transactional(readOnly = true)
    public UrlStatsResponse getStats(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));
        return urlMapper.toStatsResponse(url);
    }


    @Transactional
    public void disableUrl(String shortCode, User requestingUser) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        if (!url.getOwner().getId().equals(requestingUser.getId())) {
            log.warn("User {} attempted to disable URL owned by {}",
                    requestingUser.getUsername(), url.getOwner().getUsername());
            throw new UrlAccessDeniedException();
        }

        url.setStatus(UrlStatus.DISABLED);
        log.info("URL {} disabled by {}", shortCode, requestingUser.getUsername());
    }


    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupExpiredUrls() {
        log.info("Running scheduled URL expiry cleanup...");
        int updated = urlRepository.bulkUpdateExpiredUrls(UrlStatus.EXPIRED, Instant.now());
        log.info("Marked {} URLs as EXPIRED", updated);
    }


    private Url findActiveUrl(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        if (url.isExpired() || url.getStatus() == UrlStatus.EXPIRED) {
            throw new UrlExpiredException(shortCode);
        }

        if (url.getStatus() == UrlStatus.DISABLED) {
            throw new UrlExpiredException(shortCode); // same HTTP response (410)
        }

        return url;
    }


    private String generateUniqueCode() {
        for (int attempt = 1; attempt <= MAX_COLLISION_RETRIES; attempt++) {
            String code = codeGenerator.generate(appConfig.getShortCodeLength());

            if (!urlRepository.existsByShortCode(code)) {
                log.debug("Generated short code '{}' on attempt {}", code, attempt);
                return code;
            }

            log.warn("Short code collision on attempt {}: {}", attempt, code);
        }
        throw new ShortCodeCollisionException("Failed after " + MAX_COLLISION_RETRIES + " attempts");
    }
}

