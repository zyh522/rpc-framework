package com.zyh.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/3
 **/
@AllArgsConstructor
@Getter
public enum RpcResponseEnum {
    SUCCESS(200,"成功"),
    FAILURE(500,"失败");

    public final Integer code;
    public final String message;
}