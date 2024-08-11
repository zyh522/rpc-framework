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
public enum SerializationTypeEnum {

    KRYO((byte) 0x10,"kryo"),
    PROTOBUF((byte) 0X12,"protobuf"),
    HESSIAN((byte) 0x13, "hessian");

    private final byte code;
    private final String name;

    public static String getName(byte code){
        for (SerializationTypeEnum value : SerializationTypeEnum.values()) {
            if (value.getCode() == code){
                return value.getName();
            }
        }
        return null;
    }
}