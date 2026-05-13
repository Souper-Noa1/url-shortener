package com.url.shortener.security;

import com.url.shortener.config.AppConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final AppConfig appConfig;

    public JwtService(AppConfig appConfig) {
        this.appConfig = appConfig;
    }


    private SecretKey getSigningKey() {
        byte[] keyBytes = appConfig.getJwt().getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + appConfig.getJwt().getExpirationMs());

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the username (subject) from a JWT.
     * Throws JwtException if the token is invalid or expired.
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Validates the token — returns true if valid, false if not.
     * We catch JwtException broadly because jjwt throws subclasses
     * (ExpiredJwtException, MalformedJwtException, SignatureException, etc.)
     */
    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException e) {
            log.debug("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

