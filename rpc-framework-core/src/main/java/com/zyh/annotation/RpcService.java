package com.zyh.annotation;

import java.lang.annotation.*;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/3
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcService {

    String group() default "";

    String version() default "";
}