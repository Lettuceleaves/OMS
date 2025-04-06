package com.OMS.user_service.service;

import com.OMS.user_service.model.user;
import org.springframework.stereotype.Service;

public interface serviceInterface {

    // user information

    String insertUserInfoService(String name, String id, String grade);

    String deleteUserInfoByIdService(String id);

    String deleteUserInfoByNameService(String name);

    String updateUserInfoService(String id, String name, String grade);

    user getUserInfoByIdService(String id);

    user getUserInfoByNameService(String name);

    // score information

    String getScoreByIdService(String id);

    String getScoreByNameService(String name);

    String getAverageScoreService();
}