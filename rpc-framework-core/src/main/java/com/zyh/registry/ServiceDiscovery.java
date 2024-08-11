package com.zyh.registry;

import com.zyh.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/3
 **/
public interface ServiceDiscovery {

    /**
     * 服务发现
     *
     * @param request rpc请求
     *
     * @return InetSocketAddress 服务地址
     */
    InetSocketAddress discoveryService(RpcRequest request);
}