package com.zyh.remoting.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/1
 **/
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {

    private int requestId;
    private Object data;
    private byte messageType;
    /**
     * 序列化方式
     */
    private byte codec;
    /**
     * 压缩方式
     */
    private byte compress;
}
