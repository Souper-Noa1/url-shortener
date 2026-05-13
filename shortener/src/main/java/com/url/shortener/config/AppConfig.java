package com.url.shortener.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    private String baseUrl;
    private int shortCodeLength;
    private int defaultExpiryDays;
    private Jwt jwt = new Jwt();

    // Getters & setters required for @ConfigurationProperties binding

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public int getShortCodeLength() { return shortCodeLength; }
    public void setShortCodeLength(int shortCodeLength) { this.shortCodeLength = shortCodeLength; }

    public int getDefaultExpiryDays() { return defaultExpiryDays; }
    public void setDefaultExpiryDays(int defaultExpiryDays) { this.defaultExpiryDays = defaultExpiryDays; }

    public Jwt getJwt() { return jwt; }
    public void setJwt(Jwt jwt) { this.jwt = jwt; }

    /** Nested config class for JWT settings */
    public static class Jwt {
        private String secret;
        private long expirationMs;

        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }

        public long getExpirationMs() { return expirationMs; }
        public void setExpirationMs(long expirationMs) { this.expirationMs = expirationMs; }
    }
}

