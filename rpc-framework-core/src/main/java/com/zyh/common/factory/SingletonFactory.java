package com.zyh.common.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhuyh
 * @version v1.0
 * @description 单例工厂
 * @date 2024/8/1
 **/
public final class SingletonFactory {

    private static final Map<String, Object> BEAN_MAP = new ConcurrentHashMap<>();

    private static final Object lock = new Object();

    private SingletonFactory() {
    }

    public static <T> T getInstance(Class<T> c) {
        if (null == c) throw new IllegalArgumentException();
        String str = c.toString();
        if (BEAN_MAP.containsKey(str)) {
            return c.cast(BEAN_MAP.get(str));
        } else {
            synchronized (lock) {
                if (!BEAN_MAP.containsKey(str)) {
                    try {
                        T t = c.getDeclaredConstructor().newInstance();
                        BEAN_MAP.put(str, t);
                        return t;
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    return c.cast(BEAN_MAP.get(str));
                }
            }
        }

    }
}
