package com.OMS.user_service.repository;

import com.OMS.user_service.model.user;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface repositoryInterface {

    @Insert("INSERT INTO userinfo (name, id, grade) VALUES (#{name}, #{id}, #{grade})")
    void insertUser(user user);

    @Delete("DELETE FROM userinfo WHERE id = #{id}")
    void deleteUserById(String id);

    @Delete("DELETE FROM userinfo WHERE name = #{name}")
    void deleteUserByName(String name);

    @Update("UPDATE user SET name = #{name}, grade = #{grade} WHERE id = #{id}")
    void updateUser(user userinfo);

    @Select("SELECT * FROM userinfo WHERE id = #{id}")
    user getUserById(String id);

    @Select("SELECT * FROM userinfo WHERE name = #{name}")
    user getUserByName(String name);
}
