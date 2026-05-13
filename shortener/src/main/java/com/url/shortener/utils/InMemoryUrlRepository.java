package com.url.shortener.utils;

import com.url.shortener.domain.entities.Url;
import com.url.shortener.domain.entities.UrlStatus;



import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryUrlRepository {

    // HashMap<shortCode, Url> — O(1) lookup by short code
    private final Map<String, Url> store = new HashMap<>();

    public void save(Url url) {
        store.put(url.getShortCode(), url);
    }

    public Optional<Url> findByShortCode(String shortCode) {
        return Optional.ofNullable(store.get(shortCode));
    }

    public List<Url> findAll() {
        return new ArrayList<>(store.values());
    }


    public List<Url> findAllActive() {
        return store.values().stream()
                .filter(url -> url.getStatus() == UrlStatus.ACTIVE)
                .filter(url -> !url.isExpired())
                .collect(Collectors.toList());
    }


    public List<Url> getTopN(int n) {
        return store.values().stream()
                .sorted(Comparator.comparingLong(Url::getClickCount).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }


    public Map<String, List<Url>> groupByDomain() {
        return store.values().stream()
                .collect(Collectors.groupingBy(this::extractDomain));
    }


    public Page<Url> findPage(int pageNumber, int pageSize) {
        List<Url> all = new ArrayList<>(store.values());
        long total = all.size();

        List<Url> page = all.stream()
                .skip((long) pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

        return new Page<>(page, pageNumber, pageSize, total);
    }


    //helper
    private String extractDomain(Url url) {
        try {
            return URI.create(url.getOriginalUrl()).getHost();
        } catch (Exception e) {
            return "unknown";
        }
    }
}

