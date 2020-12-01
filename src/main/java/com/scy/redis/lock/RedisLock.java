package com.scy.redis.lock;

import com.scy.core.CollectionUtil;
import com.scy.core.ObjectUtil;
import com.scy.core.StringUtil;
import com.scy.core.UUIDUtil;
import com.scy.core.format.MessageUtil;
import com.scy.core.reflect.ClassUtil;
import com.scy.core.thread.ThreadLocalUtil;
import com.scy.redis.core.ValueOperationsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * RedisLock
 *
 * @author shichunyang
 * Created by shichunyang on 2020/12/1.
 */
@Slf4j
public class RedisLock {

    public static final Map<String, String> WATCH_MAP = new ConcurrentHashMap<>();

    public static final String REDIS_LOCK_VALUES = "redis_lock_values";

    private final ValueOperationsUtil<String, String> valueOperationsUtil;

    private final RedisTemplate<String, String> redisTemplate;

    public RedisLock(RedisTemplate<String, String> redisTemplate) {
        this.valueOperationsUtil = new ValueOperationsUtil<>(redisTemplate);
        this.redisTemplate = redisTemplate;
    }

    public void lock(String redisKey) {
        String redisValue = UUIDUtil.uuid();
        while (true) {
            Boolean isGetLock = valueOperationsUtil.setIfAbsent(redisKey, redisValue, 30_000L, TimeUnit.MILLISECONDS);
            if (isGetLock != null && isGetLock) {
                putRedisLockValue(redisValue);

                WATCH_MAP.put(redisKey, redisValue);

                log.info(MessageUtil.format("redis lock", "redisKey", redisKey, "redisValue", redisValue));
                break;
            }
        }
    }

    public void unlock(String redisKey) {
        String redisValue = getRedisLockValue();
        if (StringUtil.isEmpty(redisValue)) {
            return;
        }

        String scriptText = "if not redis.call('get', KEYS[1]) then return -1 end " +
                "if string.find(ARGV[1], redis.call('get', KEYS[1])) then return redis.call('del', KEYS[1]) else return 0 end ";

        DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setResultType(Long.class);
        defaultRedisScript.setScriptText(scriptText);
        Long result = redisTemplate.execute(defaultRedisScript, Collections.singletonList(redisKey), redisValue);
        log.info(MessageUtil.format("redis unlock", "redisKey", redisKey, "redisValue", redisValue, "result", result));
    }

    private void putRedisLockValue(String redisLockValue) {
        @SuppressWarnings(ClassUtil.UNCHECKED)
        List<String> lockValues = (ArrayList<String>) ThreadLocalUtil.get(REDIS_LOCK_VALUES);
        if (ObjectUtil.isNull(lockValues)) {
            lockValues = CollectionUtil.newArrayList(redisLockValue);
            ThreadLocalUtil.put(REDIS_LOCK_VALUES, lockValues);
            return;
        }

        lockValues.add(redisLockValue);
    }

    private String getRedisLockValue() {
        @SuppressWarnings(ClassUtil.UNCHECKED)
        List<String> lockValues = (ArrayList<String>) ThreadLocalUtil.get(REDIS_LOCK_VALUES);
        if (CollectionUtil.isEmpty(lockValues)) {
            return StringUtil.EMPTY;
        }

        return StringUtil.join(lockValues, StringUtil.COMMA);
    }
}
