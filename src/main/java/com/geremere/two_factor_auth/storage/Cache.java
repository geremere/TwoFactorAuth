package com.geremere.two_factor_auth.storage;

import com.geremere.two_factor_auth.expection.AuthException;
import com.geremere.two_factor_auth.expection.ExceptionMessage;
import lombok.Getter;

import java.util.concurrent.*;

public class Cache<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> cacheMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final long cleanUpIntervalMilliseconds;
    @Getter
    private final int limit;

    public Cache(long cleanUpIntervalMilliseconds, int limit) {
        this.cleanUpIntervalMilliseconds = cleanUpIntervalMilliseconds;
        this.limit = limit;
        scheduler.scheduleAtFixedRate(this::cleanUp, cleanUpIntervalMilliseconds, cleanUpIntervalMilliseconds, TimeUnit.MILLISECONDS);
    }

    public Cache(long cleanUpIntervalMilliseconds) {
        this.cleanUpIntervalMilliseconds = cleanUpIntervalMilliseconds;
        this.limit = -1;
        scheduler.scheduleAtFixedRate(this::cleanUp, cleanUpIntervalMilliseconds, cleanUpIntervalMilliseconds, TimeUnit.MILLISECONDS);
    }

    public void put(K key, V value) {
        cacheMap.put(key, new CacheEntry<>(value, cleanUpIntervalMilliseconds));
    }

    public boolean containsKey(K key) {
        return cacheMap.containsKey(key);
    }

    public V get(K key) {
        CacheEntry<V> entry = cacheMap.get(key);
        if (entry != null && !entry.isExpired()) {
            if (limit != -1 && entry.incrementCountOfRequest() > limit) {
                throw new AuthException(ExceptionMessage.CODE_CHECK_AMOUNT_EXCEED);
            }
            return entry.getValue();
        } else {
            cacheMap.remove(key);
            return null;
        }
    }

    public void remove(K key) {
        cacheMap.remove(key);
    }

    private void cleanUp() {
        for (K key : cacheMap.keySet()) {
            CacheEntry<V> entry = cacheMap.get(key);
            if (entry != null && entry.isExpired()) {
                cacheMap.remove(key);
            }
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}

