package com.vworks.wms.common_lib.service.impl;

import com.vworks.wms.common_lib.cache.CaffeineCacheConfig;
import com.vworks.wms.common_lib.service.CaffeineCacheService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@ConditionalOnBean(CaffeineCacheConfig.class)
public class CaffeineCacheServiceImpl implements CaffeineCacheService {
    private final CaffeineCacheManager caffeineCacheManager;

    @Override
    public void regisCache(String cacheName) {
        Set<String> cacheNames = new HashSet<>(caffeineCacheManager.getCacheNames());
        if (StringUtils.isNotBlank(cacheName)) {
            cacheNames.add(cacheName);
            caffeineCacheManager.setCacheNames(cacheNames);
        }
    }

    @Override
    public void put(String key, Object value, String cacheName) {
        Cache cache = caffeineCacheManager.getCache(cacheName);
        if (cache != null) {
            cache.put(key, value);
        }
    }

    @Override
    public Object get(String cacheName, String key) {
        Cache cache = caffeineCacheManager.getCache(cacheName);
        if (cache != null) {
            Cache.ValueWrapper valueWrapper = cache.get(key);
            return valueWrapper != null ? valueWrapper.get() : null;
        }
        return null;
    }

    @Override
    public void remove(String cacheName, String key) {
        Cache cache = caffeineCacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    @Override
    public void clear(String cacheName) {
        Cache cache = caffeineCacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    @Override
    public long size(String cacheName) {
        Cache cache = caffeineCacheManager.getCache(cacheName);
        if (cache != null && cache.getNativeCache() instanceof com.github.benmanes.caffeine.cache.Cache) {
            com.github.benmanes.caffeine.cache.Cache caffeineCache = (com.github.benmanes.caffeine.cache.Cache) cache.getNativeCache();
            return caffeineCache.estimatedSize();
        }

        return 0;
    }

    @Override
    public void put(String key, Object value, String cacheName, long duration, TimeUnit timeUnit) {
        Cache cache = caffeineCacheManager.getCache(cacheName);
        if (cache != null) {
            // Tạo cache riêng với thời gian hết hạn tùy chỉnh
            com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache =
                    (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache();

            caffeineCache.policy().expireAfterWrite().ifPresent(expiry ->
                    expiry.setExpiresAfter(duration, timeUnit)
            );

            cache.put(key, value);
        }
    }

} 