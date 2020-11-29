package com.scy.redis.core;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * ListOperationsUtil
 *
 * @author shichunyang
 * Created by shichunyang on 2020/11/29.
 */
public class ListOperationsUtil<K, V> {

    private final ListOperations<K, V> listOperations;

    public ListOperationsUtil(RedisTemplate<K, V> redisTemplate) {
        this.listOperations = redisTemplate.opsForList();
    }

    @Nullable
    public List<V> range(K key, long start, long end) {
        return listOperations.range(key, start, end);
    }

    @Nullable
    public Long size(K key) {
        return listOperations.size(key);
    }

    @Nullable
    public Long leftPush(K key, V value) {
        return listOperations.leftPush(key, value);
    }

    @SafeVarargs
    @Nullable
    public final Long leftPushAll(K key, V... values) {
        return listOperations.leftPushAll(key, values);
    }

    @Nullable
    public Long leftPushAll(K key, Collection<V> values) {
        return listOperations.leftPushAll(key, values);
    }

    @Nullable
    public Long leftPushIfPresent(K key, V value) {
        return listOperations.leftPushIfPresent(key, value);
    }

    public void set(K key, long index, V value) {
        listOperations.set(key, index, value);
    }

    @Nullable
    public Long remove(K key, long count, Object value) {
        return listOperations.remove(key, count, value);
    }

    @Nullable
    public V index(K key, long index) {
        return listOperations.index(key, index);
    }

    @Nullable
    public V leftPop(K key) {
        return listOperations.leftPop(key);
    }

    @Nullable
    public V rightPop(K key) {
        return listOperations.rightPop(key);
    }
}
