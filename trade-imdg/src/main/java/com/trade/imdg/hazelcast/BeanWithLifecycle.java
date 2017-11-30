package com.trade.imdg.hazelcast;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Bean;


@Target({ElementType.METHOD, ElementType.TYPE}) 
@Retention(RetentionPolicy.RUNTIME)
@Bean(initMethod = "start", destroyMethod = "close")
public @interface BeanWithLifecycle {

}
