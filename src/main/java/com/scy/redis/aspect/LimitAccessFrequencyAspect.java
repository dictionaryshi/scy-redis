package com.scy.redis.aspect;

import com.scy.core.CollectionUtil;
import com.scy.core.ObjectUtil;
import com.scy.core.ParamUtil;
import com.scy.core.enums.ResponseCodeEnum;
import com.scy.core.exception.BusinessException;
import com.scy.core.format.MessageUtil;
import com.scy.core.model.JoinPointBO;
import com.scy.core.spring.JoinPointUtil;
import com.scy.redis.annotation.LimitAccessFrequency;
import com.scy.redis.core.RedisTemplateUtil;
import com.scy.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.Map;

/**
 * LimitAccessFrequencyAspect
 *
 * @author shichunyang
 * Created by shichunyang on 2020/12/3.
 */
@Slf4j
@Order(LimitAccessFrequencyAspect.REQUEST_LIMIT_CHECK)
@Aspect
public class LimitAccessFrequencyAspect {

    /**
     * 请求频率校验切面
     */
    public static final int REQUEST_LIMIT_CHECK = 6000;

    private final RedisTemplateUtil<String, String> redisTemplateUtil;

    public LimitAccessFrequencyAspect(RedisTemplateUtil<String, String> redisTemplateUtil) {
        this.redisTemplateUtil = redisTemplateUtil;
    }

    @Before("@annotation(com.scy.redis.annotation.LimitAccessFrequency) && @annotation(limitAccessFrequency)")
    public void before(JoinPoint joinPoint, LimitAccessFrequency limitAccessFrequency) {
        JoinPointBO joinPointBO = JoinPointUtil.getJoinPointBO(joinPoint);

        String redisKey = buildRedisKey(joinPointBO, limitAccessFrequency);

        limitAccessFrequencyByKey(redisKey, joinPointBO, limitAccessFrequency);
    }

    private void limitAccessFrequencyByKey(String redisKey, JoinPointBO joinPointBO, LimitAccessFrequency limitAccessFrequency) {
        String scriptText = "local key = KEYS[1] " +
                "local limit = tonumber(ARGV[1]) " +

                "if redis.call('INCRBY', key, 1) == 1 then " +
                "redis.call('PEXPIRE', key, ARGV[2]) " +
                "end " +

                "local currentLimit = tonumber(redis.call('get', key) or '0') " +

                "if currentLimit > limit then " +
                "return 0 " +
                "end " +

                "return currentLimit ";

        DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setResultType(Long.class);
        defaultRedisScript.setScriptText(scriptText);
        Long result = redisTemplateUtil.execute(defaultRedisScript, Collections.singletonList(redisKey), String.valueOf(limitAccessFrequency.limit()), String.valueOf(limitAccessFrequency.timeWindow()));
        if (result == 0) {
            throw new BusinessException(ResponseCodeEnum.SYSTEM_EXCEPTION.getCode(),
                    MessageUtil.format("接口访问频率超出限制",
                            "method", joinPointBO.getMethodName(), "params", joinPointBO.getParams().toString(), "redisKey", redisKey,
                            "timeWindow", limitAccessFrequency.timeWindow(), "limit", limitAccessFrequency.limit()));
        }
    }

    private void limitAccessFrequencyByList(String redisKey, JoinPointBO joinPointBO, LimitAccessFrequency limitAccessFrequency) {
        String scriptText = "local key = KEYS[1] " +
                "local limit = tonumber(ARGV[1]) " +
                "local timestamp = tonumber(ARGV[2]) " +
                "local timeWindow = tonumber(ARGV[3]) " +

                "if redis.call('LLEN', key) >= limit then " +

                "local top = tonumber(redis.call('LRANGE', key, 0, 0)[1] or '0') " +
                "local cha = timestamp - top; " +
                "if cha <= timeWindow then " +
                "return 0 " +
                "end " +

                "end " +

                "local currentLimit = tonumber(redis.call('RPUSH', key, timestamp)) " +
                "if currentLimit <= limit then " +
                "return currentLimit " +
                "end " +

                "redis.call('LPOP', key) " +
                "return currentLimit ";

        DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setResultType(Long.class);
        defaultRedisScript.setScriptText(scriptText);
        Long result = redisTemplateUtil.execute(defaultRedisScript, Collections.singletonList(redisKey + "_list"), String.valueOf(limitAccessFrequency.limit()),
                String.valueOf(System.currentTimeMillis()), String.valueOf(limitAccessFrequency.timeWindow()));
        if (result == 0) {
            throw new BusinessException(ResponseCodeEnum.SYSTEM_EXCEPTION.getCode(),
                    MessageUtil.format("接口访问频率超出限制",
                            "method", joinPointBO.getMethodName(), "params", joinPointBO.getParams().toString(), "redisKey", redisKey,
                            "timeWindow", limitAccessFrequency.timeWindow(), "limit", limitAccessFrequency.limit()));
        }
    }

    private String buildRedisKey(JoinPointBO joinPointBO, LimitAccessFrequency limitAccessFrequency) {
        if (!limitAccessFrequency.useParam()) {
            return limitAccessFrequency.redisKey();
        }

        Map<String, Object> params = CollectionUtil.newHashMap();
        joinPointBO.getParams().forEach((paramName, paramValue) -> {
            if (ObjectUtil.isNull(paramValue) || !ParamUtil.isBasicParam(paramValue.getClass())) {
                return;
            }
            params.put(paramName, paramValue);
        });
        return RedisUtil.getRedisKey(limitAccessFrequency.redisKey(), params.toString());
    }
}
