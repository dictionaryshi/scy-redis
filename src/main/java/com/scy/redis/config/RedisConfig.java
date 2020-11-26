package com.scy.redis.config;

import com.scy.core.ObjectUtil;
import com.scy.core.StringUtil;
import com.scy.core.enums.ResponseCodeEnum;
import com.scy.core.exception.BusinessException;
import com.scy.core.spring.ApplicationContextUtil;
import com.scy.redis.core.RedisTemplateUtil;
import com.scy.redis.properties.RedisProperties;
import com.scy.redis.util.RedisUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * RedisConfig
 *
 * @author shichunyang
 * Created by shichunyang on 2020/11/9.
 */
@ConditionalOnProperty(value = "redis.enabled", havingValue = "true", matchIfMissing = true)
public class RedisConfig {

    @Bean
    public RedisProperties redisProperties() {
        RedisProperties redisProperties = Binder.get(ApplicationContextUtil.getApplicationContext().getEnvironment()).bind(RedisProperties.PREFIX, Bindable.of(RedisProperties.class)).orElse(null);

        checkRedisProperties(redisProperties);

        return redisProperties;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisProperties redisProperties) {
        return RedisUtil.buildJedisConnectionFactory(redisProperties);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.setStringSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        return redisTemplate;
    }

    private void checkRedisProperties(RedisProperties redisProperties) {
        if (ObjectUtil.isNull(redisProperties)) {
            throw new BusinessException(ResponseCodeEnum.SYSTEM_EXCEPTION.getCode(), "缺少 redis-config");
        }

        if (StringUtil.isEmpty(redisProperties.getHost())) {
            throw new BusinessException(ResponseCodeEnum.SYSTEM_EXCEPTION.getCode(), "缺少 host");
        }

        if (ObjectUtil.isNull(redisProperties.getPort())) {
            throw new BusinessException(ResponseCodeEnum.SYSTEM_EXCEPTION.getCode(), "缺少 port");
        }

        if (ObjectUtil.isNull(redisProperties.getDatabase())) {
            throw new BusinessException(ResponseCodeEnum.SYSTEM_EXCEPTION.getCode(), "缺少 database");
        }

        if (ObjectUtil.isNull(redisProperties.getMinIdle())) {
            throw new BusinessException(ResponseCodeEnum.SYSTEM_EXCEPTION.getCode(), "缺少 minIdle");
        }

        if (ObjectUtil.isNull(redisProperties.getMaxIdle())) {
            throw new BusinessException(ResponseCodeEnum.SYSTEM_EXCEPTION.getCode(), "缺少 maxIdle");
        }

        if (ObjectUtil.isNull(redisProperties.getMaxTotal())) {
            throw new BusinessException(ResponseCodeEnum.SYSTEM_EXCEPTION.getCode(), "缺少 maxTotal");
        }
    }

    @Bean
    public RedisTemplateUtil<String, String> redisTemplateUtil(RedisTemplate<String, String> redisTemplate) {
        return new RedisTemplateUtil<>(redisTemplate);
    }
}
