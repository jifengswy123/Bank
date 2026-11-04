package com.example.finance;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
//@MapperScan启动时扫描这个包下所有接口，为每一个接口用动态代理生成一个实现类对象，并注册成 Spring 的 Bean。
@MapperScan("com.example.finance.mapper")
public class FinanceApplication {

    private static final Logger log = LoggerFactory.getLogger(FinanceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(FinanceApplication.class, args);
    }
}
