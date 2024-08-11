package com.zyh.annotation;

import com.zyh.spring.scanner.CustomScannerRegistry;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/7/31
 **/
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Import(CustomScannerRegistry.class)
public @interface RpcScan {

    String[] basePackage();

}