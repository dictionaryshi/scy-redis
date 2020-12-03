package com.scy.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * LimitAccessFrequency
 *
 * @author shichunyang
 * Created by shichunyang on 2020/12/3.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LimitAccessFrequency {

    String redisKey();

    long timeWindow() default 1000L;

    int limit() default 1;

    boolean useParam() default true;
}
