//package com.zyh.remoting.protocol.tomcat;
//
//import com.zyh.remoting.RpcServer;
//import org.apache.catalina.Context;
//import org.apache.catalina.LifecycleException;
//import org.apache.catalina.Server;
//import org.apache.catalina.Service;
//import org.apache.catalina.connector.Connector;
//import org.apache.catalina.core.StandardContext;
//import org.apache.catalina.core.StandardEngine;
//import org.apache.catalina.core.StandardHost;
//import org.apache.catalina.startup.Tomcat;
//
///**
// * @author zhuyh
// * @version v1.0
// * @description 启动tomcat，接收请求
// * @date 2024/7/24
// **/
//public class HttpServer implements RpcServer {
//
//    @Override
//    public void start() {
//        Tomcat tomcat = new Tomcat();
//        Server server = tomcat.getServer();
//        Service service = server.findService("Tomcat");
//
//        Connector connector = new Connector();
//        connector.setPort(8080);
//
//        StandardEngine engine = new StandardEngine();
//        engine.setDefaultHost("localhost");
//
//        StandardHost host = new StandardHost();
//        host.setName("localhost");
//
//        String contextPath = "";
//        Context context = new StandardContext();
//        context.setPath(contextPath);
//        context.addLifecycleListener(new Tomcat.FixContextListener());
//
//        host.addChild(context);
//        engine.addChild(host);
//
//        service.setContainer(engine);
//        service.addConnector(connector);
//
//        //关键：向tomcat容器添加dispatcherServlet的servlet，DispatcherServlet需要根据接受的请求去匹配某个controller中的对应方法
//        tomcat.addServlet(contextPath, "dispatcher", new DispatcherServlet());
//        context.addServletMappingDecoded("/*", "dispatcher");  //接收的所有请求交给dispatcher处理
//
//        try {
//            tomcat.start();
//        } catch (LifecycleException e) {
//            e.printStackTrace();
//        }
//    }
//}
