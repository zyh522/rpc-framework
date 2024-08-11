package com.zyh.config;

import lombok.*;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/7/31
 **/
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceConfig {

    private String version;

    private String group;

    private Object service;

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }

    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }
}
