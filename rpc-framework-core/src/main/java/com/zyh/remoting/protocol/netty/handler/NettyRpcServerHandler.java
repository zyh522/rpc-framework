package com.zyh.remoting.protocol.netty.handler;

import com.zyh.common.constants.RpcConstants;
import com.zyh.common.enums.CompressTypeEnum;
import com.zyh.common.enums.SerializationTypeEnum;
import com.zyh.common.factory.SingletonFactory;
import com.zyh.remoting.dto.RpcMessage;
import com.zyh.remoting.dto.RpcRequest;
import com.zyh.remoting.dto.RpcResponse;
import com.zyh.remoting.handler.RpcRequestHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/1
 **/
@Slf4j
public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {

    private final RpcRequestHandler rpcRequestHandler;

    public NettyRpcServerHandler() {
        rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        try {
            if (msg instanceof RpcMessage) {
                byte messageType = ((RpcMessage) msg).getMessageType();
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setCodec(SerializationTypeEnum.HESSIAN.getCode());
                rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                // 心跳检测
                if (RpcConstants.HEARTBEAT_REQUEST_TYPE == messageType) {
                    rpcMessage.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
                    rpcMessage.setData(RpcConstants.PONG);
                } else {
                    // rpc请求
                    RpcRequest rpcRequest = (RpcRequest) ((RpcMessage) msg).getData();
                    Object result = rpcRequestHandler.handle(rpcRequest);
                    log.info("rpc invoke success,result: {}", result.toString());
                    rpcMessage.setMessageType(RpcConstants.RPC_RESPONSE_TYPE);
                    RpcResponse<Object> rpcResponse;
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                    } else {
                        rpcResponse = RpcResponse.fail();
                        log.error("netty : channel is not writable now");
                    }
                    rpcMessage.setData(rpcResponse);
                }
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (IdleState.READER_IDLE == state) {
                log.info("read idle check happened , close the connection");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("netty : server caught exception , close the connection. error message : {}", cause.getMessage());
        ctx.close();
    }
}
