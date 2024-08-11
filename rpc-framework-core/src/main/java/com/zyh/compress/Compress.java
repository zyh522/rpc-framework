package com.zyh.compress;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/7
 **/
public interface Compress {

    /**
     * 压缩
     * @param bytes 需要压缩的bytes
     * @return 压缩后的bytes
     */
    byte[] compress(byte[] bytes);

    /**
     * 解压缩
     * @param bytes 需要解压的bytes
     * @return 解压后的bytes
     */
    byte[] decompress(byte[] bytes);
}