package com.zyh.common.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/1
 **/
@Slf4j
public final class ThreadPoolFactoryUtil {

    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();

    private ThreadPoolFactoryUtil() {
    }


    /**
     * 关闭所有线程池
     */
    public static void shutDownAllThreadPool() {
        log.info("shutdown all Thread pools");
        THREAD_POOLS.entrySet().parallelStream().forEach(key -> {
            ExecutorService executorService = key.getValue();
            executorService.shutdown();
            log.info("shutdown pool {} {}", key, executorService.isTerminated());
            try {
                boolean b = executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("Thread pool is never terminated");
                executorService.shutdownNow();
            }
        });

    }

    /**
     * 创建线程工厂
     *
     * @param threadNamePrefix 线程名前缀
     * @param isDaemon         是否守护线程
     *
     * @return ThreadFactory
     */
    public static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean isDaemon) {
        if (null != threadNamePrefix) {
            if (isDaemon != null) {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").setDaemon(isDaemon).build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }
}
