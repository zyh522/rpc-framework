package com.zyh.controller;

import com.zyh.annotation.RpcScan;
import com.zyh.pojo.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/8/7
 **/
@RpcScan(basePackage = {"com.zyh"})
public class ClientApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ClientApplication.class);
        DemoController demoController = context.getBean("demoController", DemoController.class);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 5000; i++) {
            demoController.getUser(User.builder().name("xiaoyou").build());
        }
        System.out.println("耗时:" + (System.currentTimeMillis() - start));
        demoController.sayHello("xiaoyou");
    }
}
