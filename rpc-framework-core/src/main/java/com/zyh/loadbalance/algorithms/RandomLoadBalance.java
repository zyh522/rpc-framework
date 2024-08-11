package com.zyh.loadbalance.algorithms;

import com.zyh.loadbalance.AbstractLoadBalance;
import com.zyh.remoting.dto.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/3
 **/
public class RandomLoadBalance extends AbstractLoadBalance {

    @Override
    protected String loadBalance(List<String> addresses, RpcRequest request) {
        return addresses.get(new Random().nextInt(addresses.size()));
    }
}
