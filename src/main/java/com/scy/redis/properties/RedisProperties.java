package com.scy.redis.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * RedisProperties
 *
 * @author shichunyang
 * Created by shichunyang on 2020/11/9.
 */
@Getter
@Setter
@ToString
public class RedisProperties {

    public static final String PREFIX = "redis-config";

    private String host;

    private Integer port;

    private String password;

    private Integer database;

    private Integer minIdle;

    private Integer maxIdle;

    private Integer maxTotal;
}
