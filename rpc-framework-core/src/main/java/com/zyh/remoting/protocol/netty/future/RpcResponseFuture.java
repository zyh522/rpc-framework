package com.zyh.remoting.protocol.netty.future;

import com.zyh.remoting.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhuyh
 * @version v1.0
 * @description 处理Rpc响应的未来任务
 * @date 2024/8/4
 **/
public class RpcResponseFuture {

    private static final Map<String, CompletableFuture<RpcResponse<?>>> RESPONSE_FUTURES = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse<?>> future) {
        RESPONSE_FUTURES.put(requestId, future);
    }

    public void complete(RpcResponse<?> response) {
        CompletableFuture<RpcResponse<?>> future = RESPONSE_FUTURES.remove(response.getRequestId());
        if (future != null) future.complete(response);
        else throw new IllegalStateException();
    }
}
