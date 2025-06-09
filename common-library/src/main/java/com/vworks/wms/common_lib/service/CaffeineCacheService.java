package com.vworks.wms.common_lib.service;

import java.util.concurrent.TimeUnit;

public interface CaffeineCacheService {
    void regisCache(String cacheName);

    void put(String key, Object value, String cacheName);

    void put(String key, Object value, String cacheName, long duration, TimeUnit timeUnit);

    Object get(String cacheName, String key);

    void remove(String cacheName, String key);
    void clear(String cacheName);
    long size(String cacheName);
}
