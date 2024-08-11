package com.zyh.config;

import com.zyh.common.utils.ThreadPoolFactoryUtil;
import com.zyh.common.utils.ZkUtil;
import com.zyh.remoting.protocol.netty.server.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author zhuyh
 * @version v1.0
 * @description 服务关闭时，清空注册信息
 * @date 2024/8/1
 **/
@Slf4j
public class ShutdownHook {

    private static final ShutdownHook SHUTDOWN_HOOK = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {

        return SHUTDOWN_HOOK;
    }

    public void clearAll() {

        log.info("addShutdownHook for clear registry");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyRpcServer.PORT);
                ZkUtil.clearRegistry(inetSocketAddress, ZkUtil.getZkClient());
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            ThreadPoolFactoryUtil.shutDownAllThreadPool();
        }));
    }
}
