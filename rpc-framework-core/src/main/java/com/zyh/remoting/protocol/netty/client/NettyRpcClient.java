package com.zyh.remoting.protocol.netty.client;

import com.zyh.common.constants.RpcConstants;
import com.zyh.common.enums.CompressTypeEnum;
import com.zyh.common.enums.SerializationTypeEnum;
import com.zyh.common.factory.SingletonFactory;
import com.zyh.registry.ServiceDiscovery;
import com.zyh.registry.remote.zk.ZkServiceDiscovery;
import com.zyh.remoting.RpcClient;
import com.zyh.remoting.dto.RpcMessage;
import com.zyh.remoting.dto.RpcRequest;
import com.zyh.remoting.dto.RpcResponse;
import com.zyh.remoting.protocol.netty.cache.ChannelCache;
import com.zyh.remoting.protocol.netty.codec.RpcMessageDecoder;
import com.zyh.remoting.protocol.netty.codec.RpcMessageEncoder;
import com.zyh.remoting.protocol.netty.future.RpcResponseFuture;
import com.zyh.remoting.protocol.netty.handler.NettyRpcClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/3
 **/
@Slf4j
public class NettyRpcClient implements RpcClient {

    private final EventLoopGroup eventLoopGroup;
    private final Bootstrap bootstrap;
    private final ServiceDiscovery serviceDiscovery;
    private final ChannelCache channelCache;
    private final RpcResponseFuture rpcResponseFuture;

    public NettyRpcClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new LoggingHandler())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new RpcMessageEncoder());
                        pipeline.addLast(new RpcMessageDecoder());
                        pipeline.addLast(new NettyRpcClientHandler());
                    }
                });
        serviceDiscovery = SingletonFactory.getInstance(ZkServiceDiscovery.class);
        channelCache = SingletonFactory.getInstance(ChannelCache.class);
        rpcResponseFuture = SingletonFactory.getInstance(RpcResponseFuture.class);
    }

    @Override
    public Object sendRpcRequest(RpcRequest request) {
        CompletableFuture<RpcResponse<?>> resultFuture = new CompletableFuture<>();
        InetSocketAddress inetSocketAddress = serviceDiscovery.discoveryService(request);
        Channel channel = getChannel(inetSocketAddress);
        rpcResponseFuture.put(request.getRequestId(), resultFuture);
        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(SerializationTypeEnum.HESSIAN.getCode())
                .compress(CompressTypeEnum.GZIP.getCode())
                .messageType(RpcConstants.RPC_REQUEST_TYPE)
                .data(request)
                .build();
        if (channel.isActive()) {
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message : {}", rpcMessage);
                } else {
                    log.error("client send message fail :", future.cause());
                    resultFuture.completeExceptionally(future.cause());
                    future.channel().close();
                }
            });
        } else {
            throw new IllegalStateException();
        }

        return resultFuture;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelCache.get(inetSocketAddress);
        if (null == channel) {
            channel = doConnect(inetSocketAddress);
            channelCache.set(inetSocketAddress, channel);
        }
        return channel;
    }

    private Channel doConnect(InetSocketAddress inetSocketAddress) {
        try {
            CompletableFuture<Channel> channelCompletableFuture = new CompletableFuture<>();
            bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) channelCompletableFuture.complete(future.channel());
                else throw new IllegalStateException();
            });
            return channelCompletableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
