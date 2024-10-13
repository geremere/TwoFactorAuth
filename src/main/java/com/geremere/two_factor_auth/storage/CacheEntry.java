package com.geremere.two_factor_auth.storage;

import lombok.Getter;

import java.time.Instant;

public class CacheEntry<V> {
    @Getter
    private final V value;
    private final long expiryTime;
    @Getter
    private int amountOfRequest = 0;

    public CacheEntry(V value, long deltaMillis) {
        this.value = value;
        this.expiryTime = Instant.now().toEpochMilli() + deltaMillis;
    }

    public boolean isExpired() {
        return Instant.now().toEpochMilli() > expiryTime;
    }

    public int incrementCountOfRequest() {
        return ++amountOfRequest;
    }
}

