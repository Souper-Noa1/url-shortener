package com.url.shortener.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;


@Entity
@Table(name = "urls")
@Getter
@Setter
@NoArgsConstructor
public class Url implements Expirable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String originalUrl;


    @Column(nullable = false, unique = true, length = 10)
    private String shortCode;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UrlStatus status = UrlStatus.ACTIVE;

    @Column(nullable = false)
    private Long clickCount = 0L;


    @Column
    private Instant expiresAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public Url(String originalUrl, String shortCode, Instant expiresAt, User owner) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.expiresAt = expiresAt;
        this.owner = owner;
        this.status = UrlStatus.ACTIVE;
        this.clickCount = 0L;
    }


    @Override
    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }


    public void incrementClickCount() {
        this.clickCount++;
    }
}
