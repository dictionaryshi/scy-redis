package com.scy.redis.core;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Set;

/**
 * SetOperationsUtil
 *
 * @author shichunyang
 * Created by shichunyang on 2020/11/29.
 */
public class SetOperationsUtil<K, V> {

    private final SetOperations<K, V> setOperations;

    public SetOperationsUtil(RedisTemplate<K, V> redisTemplate) {
        this.setOperations = redisTemplate.opsForSet();
    }

    @SafeVarargs
    @Nullable
    public final Long add(K key, V... values) {
        return setOperations.add(key, values);
    }

    @Nullable
    public Long remove(K key, Object... values) {
        return setOperations.remove(key, values);
    }

    @Nullable
    public V pop(K key) {
        return setOperations.pop(key);
    }

    @Nullable
    public List<V> pop(K key, long count) {
        return setOperations.pop(key, count);
    }

    @Nullable
    public Long size(K key) {
        return setOperations.size(key);
    }

    @Nullable
    public Boolean isMember(K key, Object o) {
        return setOperations.isMember(key, o);
    }

    @Nullable
    public Set<V> members(K key) {
        return setOperations.members(key);
    }

    public V randomMember(K key) {
        return setOperations.randomMember(key);
    }

    @Nullable
    public Set<V> distinctRandomMembers(K key, long count) {
        return setOperations.distinctRandomMembers(key, count);
    }

    @Nullable
    public List<V> randomMembers(K key, long count) {
        return setOperations.randomMembers(key, count);
    }
}
