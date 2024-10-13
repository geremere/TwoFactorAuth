package com.geremere.two_factor_auth.service;

import com.geremere.two_factor_auth.expection.BaseException;
import com.geremere.two_factor_auth.expection.ExceptionMessage;
import com.geremere.two_factor_auth.storage.Cache;
import com.geremere.two_factor_auth.storage.InMemorySet;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CustomCacheManager {

    @Value("${spring.cache.ip.limit}")
    private Integer REQUEST_MAX_AMOUNT;

    @Value("${spring.cache.ip.value}")
    private Long CLEAN_UP_INTERVAL_IP_CACHE;

    @Value("${spring.cache.code.interval}")
    private Long CLEAN_UP_INTERVAL_CODE_CACHE;

    @Value("${spring.cache.auth}")
    private Long CLEAN_UP_INTERVAL_AUTH_USERS_CACHE;

    @Value("${spring.cache.code.limit}")
    private Integer REQUEST_LIMIT_CODE_CACHE;

    private Cache<String, String> codeCache;
    private Cache<String, Integer> ipsCache;
    private InMemorySet<String> authorizedUsersCache;

    @PostConstruct
    void init() {
        codeCache = new Cache<>(CLEAN_UP_INTERVAL_CODE_CACHE, REQUEST_LIMIT_CODE_CACHE);
        ipsCache = new Cache<>(CLEAN_UP_INTERVAL_IP_CACHE);
        authorizedUsersCache = new InMemorySet<>(CLEAN_UP_INTERVAL_AUTH_USERS_CACHE);
    }

    public void putCode(String username, String code) {
        if (isAllowedToSendCode(username)) {
            codeCache.put(username, code);
        } else {
            throw new BaseException(ExceptionMessage.WAIT_CODE);
        }

    }

    public boolean isAllowedToSendCode(String username) {
        return codeCache.get(username) == null;
    }

    public boolean request(String ip) {
        int currentAmount = ipsCache.get(ip) == null ? 1 : ipsCache.get(ip) + 1;
        ipsCache.put(ip, currentAmount);
        return currentAmount <= REQUEST_MAX_AMOUNT;
    }

    public String getCode(String username) {
        return codeCache.get(username);

    }

    public void authUser(String username) {
        codeCache.remove(username);
        authorizedUsersCache.put(username);
    }

    public String getRole(String username) {
        if (codeCache.containsKey(username)) {
            return "ROLE_PRE_AUTHORIZED";
        } else if (authorizedUsersCache.contains(username)) {
            authorizedUsersCache.remove(username);
            authorizedUsersCache.put(username);
            return "ROLE_AUTHORIZED";
        } else {
            return "ROLE_UNAUTHORIZED";
        }
    }
}
