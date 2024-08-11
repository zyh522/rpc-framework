package com.zyh.common.constants;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/3
 **/
public class RpcConstants {

    public static final byte HEARTBEAT_REQUEST_TYPE = 1;
    public static final byte HEARTBEAT_RESPONSE_TYPE = 2;
    public static final byte RPC_REQUEST_TYPE = 11;
    public static final byte RPC_RESPONSE_TYPE = 12;
    public static final String PING = "PING";
    public static final String PONG = "PONG";

    public static final byte[] MAGIC_NUMBER = {(byte) 'g', (byte) 'r', (byte) 'p', (byte) 'c'};
    public static final byte VERSION = 1;
    public static final byte TOTAL_LENGTH = 16;
    public static final int HEAD_LENGTH = 16;
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;
}
