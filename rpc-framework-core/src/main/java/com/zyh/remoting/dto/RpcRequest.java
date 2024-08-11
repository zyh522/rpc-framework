package com.zyh.remoting.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/1
 **/
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 1702679000467702945L;

    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
    private String group;
    private String version;

    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}
