package com.scy.redis.core;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * HashOperationsUtil
 *
 * @author shichunyang
 * Created by shichunyang on 2020/11/27.
 */
public class HashOperationsUtil<H, V, HK, HV> {

    private final HashOperations<H, HK, HV> hashOperations;

    public HashOperationsUtil(RedisTemplate<H, V> redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
    }

    public Long delete(H key, Object... hashKeys) {
        return hashOperations.delete(key, hashKeys);
    }

    public Boolean hasKey(H key, Object hashKey) {
        return hashOperations.hasKey(key, hashKey);
    }

    @Nullable
    public HV get(H key, Object hashKey) {
        return hashOperations.get(key, hashKey);
    }

    public List<HV> multiGet(H key, Collection<HK> hashKeys) {
        return hashOperations.multiGet(key, hashKeys);
    }

    public Set<HK> keys(H key) {
        return hashOperations.keys(key);
    }

    public Long size(H key) {
        return hashOperations.size(key);
    }

    public void putAll(H key, Map<? extends HK, ? extends HV> m) {
        hashOperations.putAll(key, m);
    }

    public void put(H key, HK hashKey, HV value) {
        hashOperations.put(key, hashKey, value);
    }

    public Boolean putIfAbsent(H key, HK hashKey, HV value) {
        return hashOperations.putIfAbsent(key, hashKey, value);
    }

    public List<HV> values(H key) {
        return hashOperations.values(key);
    }

    public Map<HK, HV> entries(H key) {
        return hashOperations.entries(key);
    }
}
