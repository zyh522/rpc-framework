package com.zyh.loadbalance;

import com.zyh.remoting.dto.RpcRequest;

import java.util.List;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/3
 **/
public abstract class AbstractLoadBalance implements LoadBalance{

    @Override
    public String selectAddress(List<String> addresses, RpcRequest request) {
        if (null == addresses || addresses.isEmpty()) return null;
        if (1 == addresses.size()) return addresses.get(0);
        return loadBalance(addresses,request);
    }

    /**
     * 实现负载均衡算法
     * @param addresses 所有服务提供者地址
     * @param request rpc请求
     * @return String 地址
     */
    protected abstract String loadBalance(List<String> addresses, RpcRequest request);
}
