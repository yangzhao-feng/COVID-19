package com.yang.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication
//// 扫描 mybatis 通用 mapper 所在的包
@MapperScan(basePackages = {"com.yang.user.mapper","com.yang.admin.mapper"})
//// 扫描所有包以及相关组件包
@ComponentScan(basePackages = {"com.yang","org.n3r.idworker","com.imooc"})
public class UserSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserSpringApplication.class);
    }
}
