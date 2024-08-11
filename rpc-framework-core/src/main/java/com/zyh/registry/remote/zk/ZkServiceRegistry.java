package com.zyh.registry.remote.zk;

import com.zyh.common.factory.SingletonFactory;
import com.zyh.common.utils.ZkUtil;
import com.zyh.config.RpcServiceConfig;
import com.zyh.registry.ServiceRegistry;
import com.zyh.registry.local.LocalServiceRegistry;
import com.zyh.remoting.protocol.netty.server.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author zhuyh
 * @version v1.0
 * @description 注册中心zk  注册服务
 * @date 2024/7/31
 **/
@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {

    private final LocalServiceRegistry localServiceRegistry = SingletonFactory.getInstance(LocalServiceRegistry.class);


    @Override
    public void registerService(RpcServiceConfig config) {
        // 本地注册
        localServiceRegistry.registerService(config);
        // 远程注册
        String servicePath = "";
        try {
            servicePath = ZkUtil.ZK_ROOT_PATH + "/" + config.getRpcServiceName() + new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyRpcServer.PORT);
        } catch (UnknownHostException e) {
            log.error("occur exception when get host address");
        }
        ZkUtil.createPersistentNode(servicePath, ZkUtil.getZkClient());
    }
}
