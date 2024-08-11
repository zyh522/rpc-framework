package com.zyh.common.utils;

import com.zyh.common.enums.RpcConfigEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/1
 **/
@Slf4j
public final class ZkUtil {

    public static final String ZK_ROOT_PATH = "/zyh-rpc";
    public static CuratorFramework zkClient;
    public static final String DEFAULT_ZK_ADDRESS = "127.0.0.1:2181";
    public static final int BASE_SLEEP_TIME = 1000;
    public static final int MAX_RETRIES = 3;
    public static final int CONNECT_TIME_OUT = 30;
    public static final Set<String> REGISTERED_PATH = ConcurrentHashMap.newKeySet();
    public static final Map<String, List<String>> serviceMap = new ConcurrentHashMap<>();


    private ZkUtil() {
    }

    /**
     * 获取zk客户端连接
     *
     * @return zkClient
     */
    public static CuratorFramework getZkClient() {

        // zkClient已经启动，直接返回
        if (null != zkClient && zkClient.getState() == CuratorFrameworkState.STARTED) return zkClient;

        Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfigEnum.RPC_CONFIG_FILE_NAME.getValue());
        String zkAddress = DEFAULT_ZK_ADDRESS;
        if (properties != null && properties.getProperty(RpcConfigEnum.RPC_ZK_ADDRESS_PREFIX.getValue()) != null) {
            zkAddress = properties.getProperty(RpcConfigEnum.RPC_ZK_ADDRESS_PREFIX.getValue());
        }
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        log.debug("rpc connect zk address : {}", zkAddress);
        zkClient = CuratorFrameworkFactory.newClient(zkAddress, retry);
        zkClient.start();
        try {
            if (!zkClient.blockUntilConnected(CONNECT_TIME_OUT, TimeUnit.SECONDS)) {
                throw new RuntimeException("time out waiting to connect zk!");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return zkClient;
    }

    /**
     * 创建持久化节点，在客户端失去连接时持久化节点不会删除
     *
     * @param path     节点路径
     * @param zkClient zk连接客户端
     */
    public static void createPersistentNode(String path, CuratorFramework zkClient) {

        try {
            if (REGISTERED_PATH.contains(path) || zkClient.checkExists().forPath(path) != null) {
                log.info("zk node already exists : {}", path);
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("zk node create successfully , node is : {}", path);
            }
            REGISTERED_PATH.add(path);
        } catch (Exception e) {
            log.error("zk create persistent node failure , node is : {}", path);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取节点信息
     *
     * @param rpcServiceName 服务名称
     * @param zkClient       zk客户端连接
     *
     * @return List<String>
     */
    public static List<String> getChildNodes(String rpcServiceName, CuratorFramework zkClient) {

        if (serviceMap.containsKey(rpcServiceName)) return serviceMap.get(rpcServiceName);

        List<String> result = null;
        String servicePath = ZK_ROOT_PATH + "/" + rpcServiceName;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            serviceMap.put(rpcServiceName, result);
            registerWatcher(rpcServiceName, zkClient);
        } catch (Exception e) {
            log.error("zk occur exception when get nodes : {}", servicePath);
        }
        return result;
    }

    /**
     * 注册监听，节点发生改变时更新缓存
     *
     * @param rpcServiceName 服务名称
     * @param zkClient       zk客户端连接
     *
     * @throws Exception 监听可能发生异常
     */
    private static void registerWatcher(String rpcServiceName, CuratorFramework zkClient) throws Exception {
        String servicePath = ZK_ROOT_PATH + "/" + rpcServiceName;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);

        PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                List<String> serviceAddress = curatorFramework.getChildren().forPath(servicePath);
                log.debug("zk node changed : {}", servicePath);
                serviceMap.put(rpcServiceName, serviceAddress);
            }
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }

    /**
     * 清空注册信息
     *
     * @param inetSocketAddress 注册地址信息
     * @param zkClient          zk客户端连接
     */
    public static void clearRegistry(InetSocketAddress inetSocketAddress, CuratorFramework zkClient) {
        REGISTERED_PATH.stream().parallel().forEach(path -> {
            if (path.endsWith(inetSocketAddress.toString())) {
                try {
                    zkClient.delete().forPath(path);
                } catch (Exception e) {
                    log.error("zk clear registry occur exception : {}", path);
                    throw new RuntimeException(e);
                }
            }
        });
        log.info("All registered services are cleared : {}", REGISTERED_PATH);
    }

}
