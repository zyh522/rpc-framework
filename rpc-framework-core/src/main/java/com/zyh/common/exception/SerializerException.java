package com.zyh.common.exception;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/7
 **/
public class SerializerException extends RuntimeException {

    private static final long serialVersionUID = 4295865521834763079L;

    public SerializerException(String msg) {
        super(msg);
    }
}
