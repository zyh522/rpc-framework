package com.zyh.proxy;

import com.zyh.common.enums.RpcErrorMessageEnum;
import com.zyh.common.enums.RpcResponseEnum;
import com.zyh.config.RpcServiceConfig;
import com.zyh.remoting.RpcClient;
import com.zyh.remoting.dto.RpcRequest;
import com.zyh.remoting.dto.RpcResponse;
import com.zyh.remoting.protocol.netty.client.NettyRpcClient;
import com.zyh.remoting.protocol.tomcat.client.SocketRpcClient;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/4
 **/
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private final RpcClient rpcClient;
    private final RpcServiceConfig rpcServiceConfig;

    public RpcClientProxy(RpcClient rpcClient, RpcServiceConfig rpcServiceConfig) {
        this.rpcClient = rpcClient;
        this.rpcServiceConfig = rpcServiceConfig;
    }


    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<?> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("proxy invoke method:{}", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .group(rpcServiceConfig.getGroup())
                .version(rpcServiceConfig.getVersion())
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes()).build();
        RpcResponse<?> rpcResponse = null;
        if (rpcClient instanceof NettyRpcClient) {
            CompletableFuture<RpcResponse<?>> rpcResponseCompletableFuture = (CompletableFuture<RpcResponse<?>>) rpcClient.sendRpcRequest(rpcRequest);
            rpcResponse = rpcResponseCompletableFuture.get();
        } else if (rpcClient instanceof SocketRpcClient) {
            rpcResponse = (RpcResponse<?>) rpcClient.sendRpcRequest(rpcRequest);
        }
        this.check(rpcRequest, rpcResponse);
        return rpcResponse.getData();
    }

    private void check(RpcRequest rpcRequest, RpcResponse<?> rpcResponse) {
        String interfaceName = "interfaceName";
        if (rpcResponse == null || rpcResponse.getCode() == null
                || !rpcResponse.getCode().equals(RpcResponseEnum.SUCCESS.getCode())) {
            throw new RuntimeException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE.getMessage() + interfaceName + ":" + rpcRequest.getInterfaceName());
        }
        if (!rpcResponse.getRequestId().equals(rpcRequest.getRequestId())) {
            throw new RuntimeException(RpcErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE.getMessage() + interfaceName + ":" + rpcRequest.getInterfaceName());
        }
    }
}
