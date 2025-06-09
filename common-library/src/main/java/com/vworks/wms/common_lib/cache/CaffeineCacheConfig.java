package com.vworks.wms.common_lib.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Configuration
@EnableCaching
public class CaffeineCacheConfig {
    @Value("${common.cache.caffeine.default.cacheName}")
    public List<String> defaultCacheNames;

    @Value("${common.cache.caffeine.default.duration}")
    public long duration;

    @Value("${common.cache.caffeine.default.timeUnit}")
    public String timeUnit;

    @Value("${common.cache.caffeine.default.maxCacheSize}")
    public long maxCacheSize;

    /**
     * Cấu hình và khởi tạo CacheManager
     * CacheManager là interface chính để quản lý cache trong Spring
     *
     * @return CacheManager đã được cấu hình
     */
    @Bean
    public CaffeineCacheManager caffeineCacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();

        // Cấu hình cache chung cho tất cả các cache
        caffeineCacheManager.setCaffeine(Caffeine.newBuilder()
                // Thời gian sống của cache
                .expireAfterWrite(duration, TimeUnit.valueOf(timeUnit.toUpperCase()))
                // Giới hạn số lượng items trong cache
                .maximumSize(maxCacheSize)
                // Bật thống kê để theo dõi hiệu suất cache
                .recordStats()
        );

        // Đăng ký tên các cache sẽ được sử dụng trong ứng dụng
        caffeineCacheManager.setCacheNames(defaultCacheNames);

        return caffeineCacheManager;
    }
} 