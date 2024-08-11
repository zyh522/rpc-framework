package com.zyh.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/1
 **/
@AllArgsConstructor
@Getter
public enum RpcConfigEnum {

    /**
     * rpc配置文件名
     */
    RPC_CONFIG_FILE_NAME("rpc.properties"),
    /**
     * rpc注册中心zk地址
     */
    RPC_ZK_ADDRESS_PREFIX("rpc.zookeeper.address");

    private final String value;
}