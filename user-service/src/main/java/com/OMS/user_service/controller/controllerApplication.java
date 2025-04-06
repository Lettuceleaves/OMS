package com.OMS.user_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class controllerApplication {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from User Service!";
    }

    @GetMapping("/userInfo/allById/{id}")
    public String getUserInfoById(@PathVariable("id") String id) {
        // Logic to get user info by ID
        return getAllByIdService(id);
    }
}
