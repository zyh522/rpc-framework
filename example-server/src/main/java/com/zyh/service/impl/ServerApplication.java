package com.zyh.service.impl;

import com.zyh.annotation.RpcScan;
import com.zyh.config.RpcServiceConfig;
import com.zyh.remoting.protocol.netty.server.NettyRpcServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/7/24
 **/
@RpcScan(basePackage = {"com.zyh"})
public class ServerApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ServerApplication.class);
        NettyRpcServer nettyRpcServer = context.getBean("nettyRpcServer", NettyRpcServer.class);
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                .service(new DemoServiceImpl())
                .version("version1")
                .group("test").build();
        nettyRpcServer.registerService(rpcServiceConfig);
        nettyRpcServer.start();
    }
}
