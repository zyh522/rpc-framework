package com.zyh.registry.remote.zk;

import com.zyh.common.enums.RpcErrorMessageEnum;
import com.zyh.common.exception.RpcException;
import com.zyh.common.factory.SingletonFactory;
import com.zyh.common.utils.ZkUtil;
import com.zyh.loadbalance.LoadBalance;
import com.zyh.loadbalance.algorithms.RandomLoadBalance;
import com.zyh.registry.ServiceDiscovery;
import com.zyh.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/3
 **/
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {

    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        loadBalance = SingletonFactory.getInstance(RandomLoadBalance.class);
    }

    @Override
    public InetSocketAddress discoveryService(RpcRequest request) {
        String rpcServiceName = request.getRpcServiceName();
        // 注册中心zk获取服务注册信息
        List<String> addressList = ZkUtil.getChildNodes(rpcServiceName, ZkUtil.getZkClient());
        if (null == addressList || addressList.isEmpty())
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        // 使用负载均衡算法选择一个服务提供者
        String address = loadBalance.selectAddress(addressList, request);
        log.debug("find service address successfully : {}", address);
        String[] str = address.split(":");
        return new InetSocketAddress(str[0], Integer.parseInt(str[1]));
    }
}
