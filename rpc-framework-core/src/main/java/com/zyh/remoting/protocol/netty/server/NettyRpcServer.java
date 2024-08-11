package com.zyh.remoting.protocol.netty.server;

import com.zyh.common.factory.SingletonFactory;
import com.zyh.common.utils.ThreadPoolFactoryUtil;
import com.zyh.config.RpcServiceConfig;
import com.zyh.config.ShutdownHook;
import com.zyh.registry.ServiceRegistry;
import com.zyh.registry.remote.zk.ZkServiceRegistry;
import com.zyh.remoting.RpcServer;
import com.zyh.remoting.protocol.netty.codec.RpcMessageDecoder;
import com.zyh.remoting.protocol.netty.codec.RpcMessageEncoder;
import com.zyh.remoting.protocol.netty.handler.NettyRpcServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/7/31
 **/
@Slf4j
@Component
public class NettyRpcServer implements RpcServer {

    public static final int PORT = 8889;

    public final ServiceRegistry serviceRegistry = SingletonFactory.getInstance(ZkServiceRegistry.class);

    public NettyRpcServer() {
    }

    public NettyRpcServer(RpcServiceConfig config) {
        registerService(config);
    }

    public void registerService(RpcServiceConfig config) {
        serviceRegistry.registerService(config);
    }

    public void start() {
        ShutdownHook.getShutdownHook().clearAll();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        DefaultEventExecutorGroup eventExecutors = new DefaultEventExecutorGroup(
                // 设置线程数：cpu 核心数 * 2
                Runtime.getRuntime().availableProcessors() * 2,
                ThreadPoolFactoryUtil.createThreadFactory("service-handler-group", false));
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_BACKLOG, 128)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new IdleStateHandler(15, 0, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new RpcMessageEncoder());
                        pipeline.addLast(new RpcMessageDecoder());
                        pipeline.addLast(eventExecutors, new NettyRpcServerHandler());
                    }
                });
        try {
            log.info("{} started and listen on port: {}", NettyRpcServer.class.getName(), NettyRpcServer.PORT);
            ChannelFuture channelFuture = serverBootstrap.bind(NettyRpcServer.PORT).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("{} started failure , error message :{} ", NettyRpcServer.class.getName(), e.getMessage());
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            eventExecutors.shutdownGracefully();
        }
    }

}
