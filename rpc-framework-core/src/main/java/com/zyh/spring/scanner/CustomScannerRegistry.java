package com.zyh.spring.scanner;

import com.zyh.annotation.RpcScan;
import com.zyh.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/7
 **/
@Slf4j
public class CustomScannerRegistry implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private final static String BASE_PACKAGE = "basePackage";

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {

        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(RpcScan.class.getName()));
        String[] basePackages = new String[0];
        if (annotationAttributes != null) {
            basePackages = annotationAttributes.getStringArray(BASE_PACKAGE);
        }
        if (basePackages.length == 0) {
            basePackages = new String[]{((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass().getPackage().getName()};
        }
        log.info("rpcScan packages:{}", basePackages);
        CustomScanner rpcServiceScanner = new CustomScanner(registry, RpcService.class);
        CustomScanner springComponentScan = new CustomScanner(registry, Component.class);
        if (resourceLoader != null) {
            rpcServiceScanner.setResourceLoader(resourceLoader);
            springComponentScan.setResourceLoader(resourceLoader);
        }
        int rpcScanCount = rpcServiceScanner.scan(basePackages);
        int springScanCount = springComponentScan.scan("com.zyh");
        log.info("rpc scan count : {}", rpcScanCount);
        log.info("spring scan count : {}", springScanCount);
    }
}
