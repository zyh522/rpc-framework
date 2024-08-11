package com.zyh.remoting.dto;

import com.zyh.common.enums.RpcResponseEnum;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/3
 **/
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcResponse<T> implements Serializable {

    private static final long serialVersionUID = 8990825143030089576L;

    /**
     * 请求id
     */
    private String requestId;
    /**
     * 响应信息
     */
    private String message;
    /**
     * 响应码
     */
    private Integer code;
    /**
     * 响应体
     */
    private T data;

    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        if (null != data) rpcResponse.setData(data);
        rpcResponse.setRequestId(requestId);
        rpcResponse.setCode(RpcResponseEnum.SUCCESS.getCode());
        rpcResponse.setMessage(RpcResponseEnum.SUCCESS.getMessage());
        return rpcResponse;
    }

    public static <T> RpcResponse<T> fail() {
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setCode(RpcResponseEnum.FAILURE.getCode());
        rpcResponse.setMessage(RpcResponseEnum.FAILURE.getMessage());
        return rpcResponse;
    }
}
