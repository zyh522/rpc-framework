package com.zyh.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/7/31
 **/
@AllArgsConstructor
@Getter
@ToString
public enum RpcErrorMessageEnum {

    SERVICE_INVOCATION_FAILURE("服务调用失败"),
    SERVICE_CAN_NOT_BE_FOUND("没找到指定服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("服务未实现任何接口"),
    REQUEST_NOT_MATCH_RESPONSE("请求与响应不匹配");

    private final String message;
}