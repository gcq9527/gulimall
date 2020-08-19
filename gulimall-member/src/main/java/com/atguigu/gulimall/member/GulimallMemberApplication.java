package com.atguigu.gulimall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 远程调用别的服务
 *  1.引入open-fegin
 *  2.编写一个接口 告诉springcloud这个接口需要调用远程服务
 *      1.声明接口的每一个方法都是调用那个远程服务的那个请求
 *  3.开启远程调用功能
 */
@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.atguigu.gulimall.member.fegin")
@SpringBootApplication
@EnableDiscoveryClient
public class GulimallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallMemberApplication.class, args);
    }

}
