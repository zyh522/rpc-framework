package com.zyh.controller;

import com.zyh.annotation.RpcReference;
import com.zyh.pojo.User;
import com.zyh.service.DemoService;
import org.springframework.stereotype.Controller;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/7/24
 **/
@Controller
public class DemoController {

    @RpcReference(group="test",version="version1")
    DemoService demoService;

    public void sayHello(String name){
        demoService.sayHello(name);
    }

    public User getUser(User u){
        // 调用远程服务
        User user = demoService.getUser(u);
        System.out.println(user.toString());
        return user;
    }

}
