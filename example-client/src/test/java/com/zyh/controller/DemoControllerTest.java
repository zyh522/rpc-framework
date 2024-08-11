package com.zyh.controller;

import com.zyh.pojo.User;
import org.junit.Test;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/7/24
 **/
public class DemoControllerTest {

    @Test
    public void testSayHello(){

        DemoController demoController = new DemoController();

        demoController.sayHello("xiaoyou");
    }

    @Test
    public void testGetUser(){

        DemoController demoController = new DemoController();

        User user = demoController.getUser(User.builder().name("xiaoyou").build());

        System.out.println(user);
    }

}
