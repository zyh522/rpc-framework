package com.zyh.netty;

import com.zyh.config.RpcServiceConfig;
import com.zyh.remoting.protocol.netty.server.NettyRpcServer;
import com.zyh.service.DemoService;
import org.junit.Test;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/3
 **/
public class NettyRpcServerTest {

    @Test
    public void testStart() {
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        rpcServiceConfig.setGroup("test1");
        rpcServiceConfig.setVersion("1.0");
        rpcServiceConfig.setService(DemoService.class);
        NettyRpcServer nettyRpcServer = new NettyRpcServer(rpcServiceConfig);
        nettyRpcServer.start();
    }
}
