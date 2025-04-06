package com.OMS.user_service.repository;

import com.OMS.user_service.model.user;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface repositoryInterface {

    @Insert("INSERT INTO userinfo (name, id, grade) VALUES (#{name}, #{id}, #{grade})")
    void insertUserInfoData(user user);

    @Delete("DELETE FROM userinfo WHERE id = #{id}")
    void deleteUserInfoDataById(String id);

    @Delete("DELETE FROM userinfo WHERE name = #{name}")
    void deleteUserInfoDataByName(String name);

    @Update("UPDATE user SET name = #{name}, grade = #{grade} WHERE id = #{id}")
    void updateUserInfoData(String id, String name, String grade);

    @Select("SELECT * FROM userinfo WHERE id = #{id}")
    user getUserInfoDataById(String id);

    @Select("SELECT * FROM userinfo WHERE name = #{name}")
    user getUserInfoDataByName(String name);
}
