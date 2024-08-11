package com.zyh.loadbalance;

import com.zyh.remoting.dto.RpcRequest;

import java.util.List;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/3
 **/
public interface LoadBalance {

    /**
     * 负载均衡，选择服务提供者地址
     *
     * @param addresses 所有服务提供者地址
     * @param request 请求信息
     *
     * @return 通过负载均衡算法选择一个服务提供者地址
     */
    String selectAddress(List<String> addresses, RpcRequest request);
}