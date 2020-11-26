package com.scy.redis.core;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * ValueOperationsUtil
 *
 * @author shichunyang
 * Created by shichunyang on 2020/11/26.
 */
public class ValueOperationsUtil<K, V> {

    private final RedisTemplate<K, V> redisTemplate;

    private final ValueOperations<K, V> valueOperations;

    public ValueOperationsUtil(RedisTemplate<K, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
        valueOperations = redisTemplate.opsForValue();
    }

    @Nullable
    public Long decrement(K key, long delta) {
        return valueOperations.decrement(key, delta);
    }

    @Nullable
    public V get(Object key) {
        return valueOperations.get(key);
    }

    @Nullable
    public V getAndSet(K key, V value) {
        return valueOperations.getAndSet(key, value);
    }

    @Nullable
    public Long increment(K key, long delta) {
        return valueOperations.increment(key, delta);
    }

    public void set(K key, V value) {
        valueOperations.set(key, value);
    }

    public void set(K key, V value, long timeout, TimeUnit unit) {
        valueOperations.set(key, value, timeout, unit);
    }

    @Nullable
    public Boolean setIfAbsent(K key, V value) {
        return valueOperations.setIfAbsent(key, value);
    }

    @Nullable
    public Boolean setIfAbsent(K key, V value, long timeout, TimeUnit unit) {
        return valueOperations.setIfAbsent(key, value, timeout, unit);
    }

    public Long increment(K key, long delta, long timeout) {
        String scriptText = "local result = redis.call('INCRBY', KEYS[1], ARGV[1]) " +
                "if result == tonumber(ARGV[1]) then redis.call('PEXPIRE', KEYS[1], ARGV[2]) end " +
                "return result";

        DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setResultType(Long.class);
        defaultRedisScript.setScriptText(scriptText);

        return redisTemplate.execute(defaultRedisScript, Collections.singletonList(key), delta + "", timeout + "");
    }
}
