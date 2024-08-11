package com.zyh.common.exception;

import com.zyh.common.enums.RpcErrorMessageEnum;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/7/31
 **/
public class RpcException extends RuntimeException {

    private static final long serialVersionUID = -4834217260815972631L;

    public RpcException(RpcErrorMessageEnum rpcExceptionMessageEnum, String msg) {
        super(rpcExceptionMessageEnum.getMessage() + ":" + msg);
    }

    public RpcException(RpcErrorMessageEnum rpcExceptionMessageEnum) {
        super(rpcExceptionMessageEnum.getMessage());
    }
}
