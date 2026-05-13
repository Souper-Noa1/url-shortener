package com.url.shortener.controller;

import com.url.shortener.domain.dtos.request.CreateUrlRequest;
import com.url.shortener.domain.dtos.response.UrlResponse;
import com.url.shortener.domain.dtos.response.UrlStatsResponse;
import com.url.shortener.domain.entities.User;
import com.url.shortener.repository.UserRepository;
import com.url.shortener.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {

    private final UrlService urlService;
    private final UserRepository userRepository;

    public UrlController(UrlService urlService, UserRepository userRepository) {
        this.urlService = urlService;
        this.userRepository = userRepository;
    }


    @PostMapping
    public ResponseEntity<UrlResponse> createUrl(
            @Valid @RequestBody CreateUrlRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User owner = resolveUser(userDetails);
        UrlResponse response = urlService.createShortUrl(request, owner);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{shortCode}")
    public ResponseEntity<UrlResponse> getUrl(@PathVariable String shortCode) {
        return ResponseEntity.ok(urlService.getUrlDetails(shortCode));
    }


    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<UrlStatsResponse> getStats(@PathVariable String shortCode) {
        return ResponseEntity.ok(urlService.getStats(shortCode));
    }


    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> disableUrl(
            @PathVariable String shortCode,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User owner = resolveUser(userDetails);
        urlService.disableUrl(shortCode, owner);
        return ResponseEntity.noContent().build();
    }

    private User resolveUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in DB"));
    }
}
