package com.zyh.remoting;

import com.zyh.remoting.dto.RpcRequest;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/3
 **/
public interface RpcClient {

    /**
     * 发送rpc请求
     *
     * @param request rpc请求体
     *
     * @return Object 响应体
     */
    Object sendRpcRequest(RpcRequest request);
}