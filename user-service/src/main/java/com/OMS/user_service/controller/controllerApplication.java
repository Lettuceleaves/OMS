package com.OMS.user_service.controller;

import com.OMS.user_service.model.user;
import com.OMS.user_service.service.serviceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class controllerApplication {

    @Autowired
    private serviceInterface service;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from User Service!";
    }

    @GetMapping("/userInfo/allById/{id}")
    public user getUserInfoById(@PathVariable("id") String id) {
        // Logic to get user info by
        user u = service.getUserInfoByIdService(id);
        return u;
    }

    @GetMapping("/userInfo/allByName/{name}")
    public user getUserInfoByName(@PathVariable("name") String name) {
        // Logic to get user info by name
        user u = service.getUserInfoByNameService(name);
        return u;
    }

}
