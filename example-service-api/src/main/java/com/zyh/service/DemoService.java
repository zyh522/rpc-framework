package com.zyh.service;

import com.zyh.pojo.User;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/7/24
 **/
public interface DemoService {

    void sayHello(String name);

    User getUser(User user);
}