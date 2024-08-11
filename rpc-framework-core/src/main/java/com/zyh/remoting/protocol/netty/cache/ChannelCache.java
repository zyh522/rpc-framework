package com.zyh.remoting.protocol.netty.cache;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/4
 **/
public class ChannelCache {

    private final Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    public ChannelCache() {
    }

    public Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        if (channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            if (null != channel && channel.isActive()) {
                return channel;
            }
            channelMap.remove(key);
        }
        return null;
    }

    public void set(InetSocketAddress inetSocketAddress, Channel channel) {
        channelMap.put(inetSocketAddress.toString(), channel);
    }

    public void remove(InetSocketAddress inetSocketAddress) {
        channelMap.remove(inetSocketAddress.toString());
    }
}
