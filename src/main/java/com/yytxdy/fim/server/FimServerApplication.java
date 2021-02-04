package com.yytxdy.fim.server;

import com.yytxdy.fim.server.netty.NettyServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages = "com.yytxdy.fim.server.mapper")
public class FimServerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(FimServerApplication.class, args);
        NettyServer nettyServer = context.getBean(NettyServer.class);
        nettyServer.run();
    }

}
