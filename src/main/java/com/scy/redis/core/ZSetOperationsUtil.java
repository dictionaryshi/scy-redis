package com.scy.redis.core;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.lang.Nullable;

import java.util.Set;

/**
 * ZSetOperationsUtil
 *
 * @author shichunyang
 * Created by shichunyang on 2020/11/30.
 */
public class ZSetOperationsUtil<K, V> {

    private final ZSetOperations<K, V> zsetOperations;

    public ZSetOperationsUtil(RedisTemplate<K, V> redisTemplate) {
        this.zsetOperations = redisTemplate.opsForZSet();
    }

    @Nullable
    public Boolean add(K key, V value, double score) {
        return zsetOperations.add(key, value, score);
    }

    @Nullable
    public Long add(K key, Set<ZSetOperations.TypedTuple<V>> tuples) {
        return zsetOperations.add(key, tuples);
    }

    @Nullable
    public Long remove(K key, Object... values) {
        return zsetOperations.remove(key, values);
    }

    @Nullable
    public Double incrementScore(K key, V value, double delta) {
        return zsetOperations.incrementScore(key, value, delta);
    }

    @Nullable
    public Set<V> range(K key, long start, long end) {
        return zsetOperations.range(key, start, end);
    }

    @Nullable
    public Set<ZSetOperations.TypedTuple<V>> rangeWithScores(K key, long start, long end) {
        return zsetOperations.rangeWithScores(key, start, end);
    }

    @Nullable
    public Set<V> rangeByScore(K key, double min, double max) {
        return zsetOperations.rangeByScore(key, min, max);
    }

    @Nullable
    public Set<ZSetOperations.TypedTuple<V>> rangeByScoreWithScores(K key, double min, double max) {
        return zsetOperations.rangeByScoreWithScores(key, min, max);
    }

    @Nullable
    public Set<V> rangeByScore(K key, double min, double max, long offset, long count) {
        return zsetOperations.rangeByScore(key, min, max, offset, count);
    }

    @Nullable
    public Set<ZSetOperations.TypedTuple<V>> rangeByScoreWithScores(K key, double min, double max, long offset, long count) {
        return zsetOperations.rangeByScoreWithScores(key, min, max, offset, count);
    }

    @Nullable
    public Long count(K key, double min, double max) {
        return zsetOperations.count(key, min, max);
    }

    @Nullable
    public Long zCard(K key) {
        return zsetOperations.zCard(key);
    }

    @Nullable
    public Double score(K key, Object o) {
        return zsetOperations.score(key, o);
    }

    @Nullable
    public Long removeRange(K key, long start, long end) {
        return zsetOperations.removeRange(key, start, end);
    }

    @Nullable
    public Long removeRangeByScore(K key, double min, double max) {
        return zsetOperations.removeRangeByScore(key, min, max);
    }
}
