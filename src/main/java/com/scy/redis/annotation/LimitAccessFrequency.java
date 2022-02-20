package com.scy.redis.annotation;

import java.lang.annotation.*;

/**
 * LimitAccessFrequency
 *
 * @author shichunyang
 * Created by shichunyang on 2020/12/3.
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LimitAccessFrequency {

    String redisKey();

    long timeWindow() default 1000L;

    int limit() default 1;

    boolean useParam() default true;
}
