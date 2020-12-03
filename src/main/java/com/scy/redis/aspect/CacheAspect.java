package com.scy.redis.aspect;

import com.scy.core.CollectionUtil;
import com.scy.core.ObjectUtil;
import com.scy.core.ParamUtil;
import com.scy.core.format.MessageUtil;
import com.scy.core.json.JsonUtil;
import com.scy.core.model.JoinPointBO;
import com.scy.core.spring.JoinPointUtil;
import com.scy.redis.annotation.Cache;
import com.scy.redis.core.ValueOperationsUtil;
import com.scy.redis.lock.RedisLock;
import com.scy.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * CacheAspect
 *
 * @author shichunyang
 * Created by shichunyang on 2020/12/2.
 */
@Slf4j
@Order(CacheAspect.CACHE)
@Aspect
public class CacheAspect {

    /**
     * 缓存切面
     */
    public static final int CACHE = 18000;

    private final ValueOperationsUtil<String, String> valueOperationsUtil;

    private final RedisLock redisLock;

    public CacheAspect(ValueOperationsUtil<String, String> valueOperationsUtil, RedisLock redisLock) {
        this.valueOperationsUtil = valueOperationsUtil;
        this.redisLock = redisLock;
    }

    @Around("@annotation(com.scy.redis.annotation.Cache) && @annotation(cache)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, Cache cache) throws Throwable {
        JoinPointBO joinPointBO = JoinPointUtil.getJoinPointBO(proceedingJoinPoint);

        String redisKey = buildRedisKey(joinPointBO, cache);

        Object result = getResultFromCache(redisKey, joinPointBO);
        if (!ObjectUtil.isNull(result)) {
            log.info(MessageUtil.format("缓存命中", "redisKey", redisKey, "result", result));
            return result;
        }

        String lockKey = redisKey + "_lock";
        try {
            redisLock.lock(lockKey);

            result = getResultFromCache(redisKey, joinPointBO);
            if (!ObjectUtil.isNull(result)) {
                log.info(MessageUtil.format("缓存命中", "redisKey", redisKey, "result", result));
                return result;
            }

            result = proceedingJoinPoint.proceed();
            valueOperationsUtil.setIfAbsent(redisKey, JsonUtil.object2Json(result), cache.timeout(), TimeUnit.MILLISECONDS);
            return result;
        } finally {
            redisLock.unlock(lockKey);
        }
    }

    private Object getResultFromCache(String redisKey, JoinPointBO joinPointBO) {
        String json = valueOperationsUtil.get(redisKey);
        if (ObjectUtil.isNull(json)) {
            return null;
        }

        return JsonUtil.json2Object(json, joinPointBO.getMethod());
    }

    private String buildRedisKey(JoinPointBO joinPointBO, Cache cache) {
        Map<String, Object> params = CollectionUtil.newHashMap();
        joinPointBO.getParams().forEach((paramName, paramValue) -> {
            if (ObjectUtil.isNull(paramValue) || !ParamUtil.isBasicParam(paramValue.getClass())) {
                return;
            }
            params.put(paramName, paramValue);
        });
        return RedisUtil.getRedisKey(cache.redisKey(), params.toString());
    }
}
