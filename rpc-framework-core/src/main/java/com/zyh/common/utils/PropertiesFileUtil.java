package com.zyh.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author zhuyh
 * @version v1.0
 * @description 读取配置文件工具类
 * @date 2024/8/1
 **/
@Slf4j
public final class PropertiesFileUtil {

    private PropertiesFileUtil() {
    }

    /**
     * 按文件名读取配置文件
     *
     * @param fileName 文件名
     *
     * @return Properties
     */
    public static Properties readPropertiesFile(String fileName) {

        Properties properties = null;
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            log.error("occur Exception when read properties file : {}", fileName);
        }
        return properties;
    }


}
