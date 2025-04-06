package com.OMS.user_service.model;

import lombok.Data;

@Data
public class user {
    private String name;
//    private String email;
//    private String phone;
//    private String address;
//    private String role;
//    private String status;
    private String id;
    private String grade;

    public user(String name, String id, String grade) {
        this.name = name;
        this.id = id;
        this.grade = grade;
    }
}
