package com.zyh.registry;

import com.zyh.config.RpcServiceConfig;

/**
 * @author zhuyh
 * @version v1.0
 * @description 服务注册接口，所有服务注册必须实现此接口
 * @date 2024/7/31
 **/
public interface ServiceRegistry {


    /**
     * 注册服务
     *
     * @param config 服务配置信息
     */
    void registerService(RpcServiceConfig config);
}