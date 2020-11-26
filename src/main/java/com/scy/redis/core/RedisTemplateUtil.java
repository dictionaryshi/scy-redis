package com.scy.redis.core;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * RedisTemplateUtil
 *
 * @author shichunyang
 * Created by shichunyang on 2020/11/26.
 */
@AllArgsConstructor
public class RedisTemplateUtil<K, V> {

    private final RedisTemplate<K, V> redisTemplate;

    /**
     * 删除给定的一个或多个 key
     */
    public Long delete(Collection<K> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 检查给定key是否存在
     */
    public Boolean hasKey(K key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 为给定key设置生存时间
     */
    public Boolean expire(K key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    public Boolean expireAt(K key, Date date) {
        return redisTemplate.expireAt(key, date);
    }

    public Long getExpire(K key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    public <T> T execute(RedisScript<T> script, List<K> keys, Object... args) {
        return redisTemplate.execute(script, keys, args);
    }
}
