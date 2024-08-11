package com.zyh.spring;

import com.zyh.annotation.RpcReference;
import com.zyh.annotation.RpcService;
import com.zyh.common.factory.SingletonFactory;
import com.zyh.config.RpcServiceConfig;
import com.zyh.proxy.RpcClientProxy;
import com.zyh.registry.ServiceRegistry;
import com.zyh.registry.remote.zk.ZkServiceRegistry;
import com.zyh.remoting.RpcClient;
import com.zyh.remoting.protocol.netty.client.NettyRpcClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/3
 **/
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final ServiceRegistry registry;
    private final RpcClient rpcClient;

    public SpringBeanPostProcessor() {
        this.registry = SingletonFactory.getInstance(ZkServiceRegistry.class);
        this.rpcClient = SingletonFactory.getInstance(NettyRpcClient.class);
    }

    /**
     * 初始化前扫描注解RpcService 命中的service注册到注册中心
     *
     * @param bean     对象
     * @param beanName 名称
     *
     * @return bean
     * @throws BeansException 异常
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(RpcService.class)) {
            RpcService annotation = beanClass.getAnnotation(RpcService.class);
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .group(annotation.group())
                    .version(annotation.version())
                    .service(bean)
                    .build();
            registry.registerService(rpcServiceConfig);
        }
        return bean;
    }

    /**
     * 初始化后扫描RpcReference注解，命中的字段为远程调用服务 创建代理对象
     *
     * @param bean     对象
     * @param beanName 名称
     *
     * @return bean
     * @throws BeansException 异常
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Field[] declaredFields = beanClass.getDeclaredFields();
        for (Field field : declaredFields) {
            RpcReference annotation = field.getAnnotation(RpcReference.class);
            if (null != annotation) {
                // 创建代理对象
                RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                        .group(annotation.group())
                        .version(annotation.version())
                        .build();
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceConfig);
                Object proxy = rpcClientProxy.getProxy(field.getType());
                field.setAccessible(true);
                try {
                    field.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }
}
