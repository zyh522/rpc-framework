package com.zyh.serializer.hessian;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.zyh.common.exception.SerializerException;
import com.zyh.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/7
 **/
public class HessianSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            HessianOutput hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new SerializerException("Hessian serialize exception : " + e.getMessage());
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            HessianInput hessianInput = new HessianInput(byteArrayInputStream);
            Object o = hessianInput.readObject();
            return clazz.cast(o);
        } catch (IOException e) {
            throw new SerializerException("Hessian deserialize exception : " + e.getMessage());
        }
    }
}
