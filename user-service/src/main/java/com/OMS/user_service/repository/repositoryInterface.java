package com.OMS.user_service.repository;

import com.OMS.user_service.model.user;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface repositoryInterface {

    @Insert("INSERT INTO user (name, id, grade) VALUES (#{name}, #{id}, #{grade})")
    void insertUser(user user);

    @Delete("DELETE FROM user WHERE id = #{id}")
    void deleteUser(int id);

    @Update("UPDATE user SET name = #{name}, grade = #{grade} WHERE id = #{id}")
    void updateUser(user user);

    @Select("SELECT * FROM user WHERE id = #{id}")
    user getUserById(int id);

    @Select("SELECT * FROM user WHERE name = #{name}")
    user getUserByName(String name);

    @Select("SELECT * FROM user WHERE grade = #{grade}")
    user getUserByGrade(String grade);
}
