package com.OMS.user_service.service;

import com.OMS.user_service.model.user;

public interface serviceInterface {

    String insertUserService(String name, String id, String grade);

    String deleteUserByIdService(String id);

    String deleteUserByNameService(String name);

    String updateUserService(String id, String name, String grade);

    user getAllByIdService(String id);

    user getAllByNameService(String name);
}
