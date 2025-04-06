package com.OMS.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "scores-check")
@Repository
public interface clientScoreInterface {

    @GetMapping("/userInfo/scoreById/{id}")
    String getScoreById(@PathVariable("id") String id);

    @GetMapping("/userInfo/scoreByName/{name}")
    String getScoreByName(@PathVariable("name") String name);

    @GetMapping("/userInfo/averageScore")
    String getAverageScore();
}
