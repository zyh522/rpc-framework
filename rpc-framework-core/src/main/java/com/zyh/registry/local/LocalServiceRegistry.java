package com.zyh.registry.local;

import com.zyh.common.enums.RpcErrorMessageEnum;
import com.zyh.common.exception.RpcException;
import com.zyh.config.RpcServiceConfig;
import com.zyh.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhuyh
 * @version v1.0
 * @description 本地缓存注册
 * @date 2024/7/31
 **/
@Slf4j
public class LocalServiceRegistry implements ServiceRegistry {

    private final Map<String, Object> serviceMap;

    public LocalServiceRegistry() {
        serviceMap = new ConcurrentHashMap<>();
    }

    @Override
    public void registerService(RpcServiceConfig config) {
        String rpcServiceName = config.getRpcServiceName();
        // 本地注册缓存已注册此service
        if (serviceMap.containsKey(rpcServiceName)) {
            return;
        }
        serviceMap.put(rpcServiceName, config.getService());
        log.info("LocalRegistry Add service: {}, interfaces: {}", rpcServiceName, config.getService().getClass().getInterfaces());
    }

    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (service == null) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }
}
