package com.OMS.user_service.service;

import com.OMS.user_service.model.user;
import com.OMS.user_service.repository.repositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class serviceApplication implements serviceInterface {

    @Autowired
    private repositoryInterface repository;

    public String insertUserService(String name, String id, String grade) {
        user newUser = new user(name, id, grade);
        repository.insertUser(newUser);
        return "User inserted successfully";
    }

    public String deleteUserByIdService(String id) {
        // Logic to delete user by ID
        repository.deleteUserById(id);
        return "User deleted successfully";
    }

    public String deleteUserByNameService(String name) {
        // Logic to delete user by name
        repository.deleteUserByName(name);
        return "User deleted successfully";
    }

    public String updateUserService(String id, String name, String grade) {
        user updatedUser = new user(name, id, grade);
        repository.updateUser(updatedUser);
        return "User updated successfully";
    }

    public user getAllByIdService(String id) {
        return repository.getUserById(id);
    }

    public user getAllByNameService(String name) {
        return repository.getUserByName(name);
    }
}
