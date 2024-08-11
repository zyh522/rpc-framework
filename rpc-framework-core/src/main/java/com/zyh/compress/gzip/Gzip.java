package com.zyh.compress.gzip;

import com.zyh.compress.Compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/7
 **/
public class Gzip implements Compress {
    @Override
    public byte[] compress(byte[] bytes) {
        if (null == bytes){
            throw new NullPointerException("Gzip compress bytes is null");
        }
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)){
            gzipOutputStream.write(bytes);
            gzipOutputStream.flush();
            gzipOutputStream.finish();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Gzip compress error :",e);
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        if (null == bytes){
            throw new NullPointerException("Gzip decompress bytes is null");
        }
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)){
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = gzipInputStream.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer,0,read);
            }
            return byteArrayOutputStream.toByteArray();
        }catch (IOException e){
            throw new RuntimeException("Gzip decompress error :",e);
        }
    }
}
