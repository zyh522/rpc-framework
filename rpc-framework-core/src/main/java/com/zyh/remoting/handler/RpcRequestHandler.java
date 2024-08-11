package com.zyh.remoting.handler;

import com.zyh.common.factory.SingletonFactory;
import com.zyh.registry.local.LocalServiceRegistry;
import com.zyh.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zhuyh
 * @version v1.0
 * @description rpc请求处理器
 * @date 2024/8/1
 **/
@Slf4j
public class RpcRequestHandler {

    private final LocalServiceRegistry localServiceRegistry;

    public RpcRequestHandler() {
        localServiceRegistry = SingletonFactory.getInstance(LocalServiceRegistry.class);
    }

    /**
     * 处理请求
     *
     * @param request rpc请求
     *
     * @return Object 返回结果
     */
    public Object handle(RpcRequest request) {
        log.debug("receive rpc request, service : {} , method : {}", request.getInterfaceName(), request.getMethodName());
        Object service = localServiceRegistry.getService(request.getRpcServiceName());
        return invokeTargetMethod(request, service);
    }

    private Object invokeTargetMethod(RpcRequest request, Object service) {
        Object result;
        try {
            Method method = service.getClass().getDeclaredMethod(request.getMethodName(), request.getParamTypes());
            result = method.invoke(service, request.getParameters());
            log.info("service:{} method : {} invoke successfully", request.getInterfaceName(), request.getMethodName());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
