package com.scy.redis.annotation;

import java.lang.annotation.*;

/**
 * Cache
 *
 * @author shichunyang
 * Created by shichunyang on 2020/12/2.
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {

    String redisKey();

    long timeout() default 300_000L;
}
