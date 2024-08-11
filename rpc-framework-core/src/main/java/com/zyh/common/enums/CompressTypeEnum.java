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
public enum CompressTypeEnum {

    GZIP((byte)0x10,"gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.getName();
            }
        }
        return null;
    }
}