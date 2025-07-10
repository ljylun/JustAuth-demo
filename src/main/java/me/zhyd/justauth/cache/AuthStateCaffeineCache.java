package me.zhyd.justauth.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.zhyd.oauth.cache.AuthCacheConfig;
import me.zhyd.oauth.cache.AuthStateCache;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 基于Caffeine的state缓存实现
 *
 * @author peter
 * @version 1.0
 * @date 2023/10/01
 */
@Component
public class AuthStateCaffeineCache implements AuthStateCache {
    private Cache<String, String> cache;

    @PostConstruct
    public void init() {
        // 初始化本地缓存，设置初始容量为100，最大条目数为500，过期时间为默认3分钟
        cache = Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterWrite(AuthCacheConfig.timeout, TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * 存入缓存，默认3分钟过期时间
     *
     * @param key   缓存key
     * @param value 缓存内容
     */
    @Override
    public void cache(String key, String value) {
        cache.put(key, value);
    }

    /**
     * 存入缓存并指定过期时间
     *
     * @param key     缓存key
     * @param value   缓存内容
     * @param timeout 过期时间（毫秒）
     */
    @Override
    public void cache(String key, String value, long timeout) {
        cache = Caffeine.newBuilder()
                .expireAfterWrite(timeout, TimeUnit.MILLISECONDS)
                .build();
        cache.put(key, value);
    }

    /**
     * 获取缓存内容
     *
     * @param key 缓存key
     * @return 缓存内容
     */
    @Override
    public String get(String key) {
        return cache.getIfPresent(key);
    }

    /**
     * 检查是否存在指定key的缓存
     *
     * @param key 缓存key
     * @return true：存在且未过期；false：不存在或已过期
     */
    @Override
    public boolean containsKey(String key) {
        return cache.getIfPresent(key) != null;
    }
}