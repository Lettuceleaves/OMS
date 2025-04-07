package com.OMS.score_check.controller;

import com.OMS.score_check.service.serviceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class clientScoreInterface {

    @Autowired
    private serviceInterface service;

    @GetMapping("/userInfo/scoreById/{id}")
    String getScoreById(@PathVariable("id") String id) {
        // Logic to get user info by id
        return service.getScoreByIdService(id);
    }

    @GetMapping("/userInfo/scoreByName/{name}")
    String getScoreByName(@PathVariable("name") String name) {
        // Logic to get user info by name
        return service.getScoreByNameService(name);
    }

    @GetMapping("/userInfo/averageScore")
    String getAverageScore() {
        // Logic to get user info by name
        return service.getAverageScoreService();
    }
}