package com.scy.redis.util;

import com.scy.redis.properties.RedisProperties;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

/**
 * RedisUtil
 *
 * @author shichunyang
 * Created by shichunyang on 2020/11/10.
 */
public class RedisUtil {

    public static JedisPoolConfig buildJedisPoolConfig(RedisProperties redisProperties) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 资源池中的最大连接数
        jedisPoolConfig.setMaxTotal(redisProperties.getMaxTotal());
        // 资源池允许的最大空闲连接数
        jedisPoolConfig.setMaxIdle(redisProperties.getMaxIdle());
        // 资源池确保的最少空闲连接数
        jedisPoolConfig.setMinIdle(redisProperties.getMinIdle());
        // 当资源池连接用尽后, 调用者的最大等待时间
        jedisPoolConfig.setMaxWaitMillis(10_000);
        // 资源池中资源的最小空闲时间
        jedisPoolConfig.setMinEvictableIdleTimeMillis(300_000L);
        // 向资源池借用连接时是否做连接有效性检测
        jedisPoolConfig.setTestOnBorrow(Boolean.FALSE);
        // 向资源池归还连接时是否做连接有效性检测
        jedisPoolConfig.setTestOnReturn(Boolean.FALSE);
        // 是否开启空闲资源检测
        jedisPoolConfig.setTestWhileIdle(Boolean.TRUE);
        // 空闲资源的检测周期
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(60_000L);
        return jedisPoolConfig;
    }

    public static JedisConnectionFactory buildJedisConnectionFactory(RedisProperties redisProperties) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
        redisStandaloneConfiguration.setPassword(redisProperties.getPassword());

        JedisPoolConfig jedisPoolConfig = buildJedisPoolConfig(redisProperties);
        JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder()
                .usePooling().poolConfig(jedisPoolConfig).build();
        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
    }
}
