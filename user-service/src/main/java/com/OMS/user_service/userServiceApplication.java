package com.OMS.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class userServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(userServiceApplication.class, args);
        System.out.println("Hello World!");
    }
}