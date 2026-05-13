package com.url.shortener.repository;

import com.url.shortener.domain.entities.Url;
import com.url.shortener.domain.entities.UrlStatus;
import com.url.shortener.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByShortCode(String shortCode);


    boolean existsByShortCode(String shortCode);


    List<Url> findByOwnerId(Long ownerId);

    @Query("SELECT u FROM Url u WHERE u.expiresAt IS NOT NULL AND u.expiresAt BETWEEN :now AND :cutoff")
    List<Url> findUrlsExpiringBetween(@Param("now") Instant now, @Param("cutoff") Instant cutoff);


    @Modifying
    @Query("UPDATE Url u SET u.status = :status WHERE u.expiresAt < :now AND u.status = 'ACTIVE'")
    int bulkUpdateExpiredUrls(@Param("status") UrlStatus status, @Param("now") Instant now);
}
