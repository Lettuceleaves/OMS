package com.OMS.score_check.service;

import com.OMS.score_check.repository.repositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class serviceApplication implements serviceInterface {
    @Autowired
    private repositoryInterface repository;

    public String getScoreByIdService(String id) {
        return repository.getScoreById(id);
    }

    public String getScoreByNameService(String name) {
        return repository.getScoreByName(name);
    }

    public String getAverageScoreService() {
        return repository.getAverageScore();
    }
}
