package com.zyh.remoting.protocol.netty.handler;

import com.zyh.common.constants.RpcConstants;
import com.zyh.common.enums.CompressTypeEnum;
import com.zyh.common.enums.SerializationTypeEnum;
import com.zyh.common.factory.SingletonFactory;
import com.zyh.remoting.dto.RpcMessage;
import com.zyh.remoting.dto.RpcResponse;
import com.zyh.remoting.protocol.netty.client.NettyRpcClient;
import com.zyh.remoting.protocol.netty.future.RpcResponseFuture;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/3
 **/
@Slf4j
public class NettyRpcClientHandler extends ChannelInboundHandlerAdapter {

    private final RpcResponseFuture rpcResponseFuture;

    private final NettyRpcClient nettyRpcClient;

    public NettyRpcClientHandler() {
        rpcResponseFuture = SingletonFactory.getInstance(RpcResponseFuture.class);
        nettyRpcClient = SingletonFactory.getInstance(NettyRpcClient.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof RpcMessage) {
                RpcMessage rpcMessage = (RpcMessage) msg;
                byte messageType = rpcMessage.getMessageType();
                if (RpcConstants.HEARTBEAT_RESPONSE_TYPE == messageType) {
                    log.info("heart beat response : {}", rpcMessage.getData());
                } else if (RpcConstants.RPC_RESPONSE_TYPE == messageType) {
                    rpcResponseFuture.complete((RpcResponse<?>) rpcMessage.getData());
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            // 发生写空闲事件时，发送心跳检查
            if (IdleState.WRITER_IDLE == state) {
                RpcMessage rpcMessage = RpcMessage.builder()
                        .codec(SerializationTypeEnum.HESSIAN.getCode())
                        .compress(CompressTypeEnum.GZIP.getCode())
                        .messageType(RpcConstants.HEARTBEAT_REQUEST_TYPE)
                        .data(RpcConstants.PING)
                        .build();
                Channel channel = nettyRpcClient.getChannel(((InetSocketAddress) ctx.channel().remoteAddress()));
                channel.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("netty client exception:{}", cause.getMessage());
        ctx.close();
    }
}
