package com.OMS.user_service.service;

import com.OMS.user_service.client.clientScoreInterface;
import com.OMS.user_service.model.user;
import com.OMS.user_service.repository.repositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class serviceApplication implements serviceInterface {

    @Autowired
    private repositoryInterface repository;

    @Autowired
    private clientScoreInterface clientScore;

    public String insertUserInfoService(String name, String id, String grade) {
        user newUser = new user(name, id, grade);
        repository.insertUserInfoData(newUser);
        return "User inserted successfully";
    }

    public String deleteUserInfoByIdService(String id) {
        // Logic to delete user by ID
        repository.deleteUserInfoDataById(id);
        return "User deleted successfully";
    }

    public String deleteUserInfoByNameService(String name) {
        // Logic to delete user by name
        repository.deleteUserInfoDataByName(name);
        return "User deleted successfully";
    }

    public String updateUserInfoService(String name, String id, String grade) {
        // Logic to update user information
        repository.updateUserInfoData(name, id, grade);
        return "User updated successfully";
    }

    public user getUserInfoByIdService(String id) {
        return repository.getUserInfoDataById(id);
    }

    public user getUserInfoByNameService(String name) {
        return repository.getUserInfoDataByName(name);
    }

    // Score information methods

    public String getScoreByIdService(String id) {
        return clientScore.getScoreById(id);
    }

    public String getScoreByNameService(String name) {
        return clientScore.getScoreByName(name);
    }

    public String getAverageScoreService() {
        return clientScore.getAverageScore();
    }
}
