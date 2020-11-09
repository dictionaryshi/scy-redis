package com.scy.redis.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * RedisConfig
 *
 * @author shichunyang
 * Created by shichunyang on 2020/11/9.
 */
@ConditionalOnProperty(value = "redis.enabled", havingValue = "true", matchIfMissing = true)
public class RedisConfig {
}
