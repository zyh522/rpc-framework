package com.zyh.service.impl;

import com.zyh.annotation.RpcService;
import com.zyh.pojo.User;
import com.zyh.service.DemoService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/7/24
 **/
@Slf4j
@RpcService
public class DemoServiceImpl implements DemoService {

    @Override
    public void sayHello(String name) {
        log.info("DemoServiceImpl,sayHello方法收到请求，请求参数：{}", name);
        System.out.println("Hello," + name);
    }

    @Override
    public User getUser(User user) {
        log.info("DemoServiceImpl，getUser方法收到请求，请求参数：{}", user);
        return new User(user.getName(), 20);
    }
}
