package icu.baidu.aria2.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<CaffeineCache> caches = new ArrayList<>();
        caches.add(new CaffeineCache("cache-0", Caffeine.newBuilder()
                .recordStats()
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .maximumSize(100)
                .build()
        ));
        caches.add(new CaffeineCache("cache-1", Caffeine.newBuilder()
                .recordStats()
                .expireAfterWrite(86300, TimeUnit.SECONDS)
                .maximumSize(1000)
                .build()
        ));
        cacheManager.setCaches(caches);
        return cacheManager;
    }
}
