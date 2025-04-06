package com.OMS.score_check.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Mapper
@Repository
public interface repositoryInterface {
    @Select("SELECT score FROM score WHERE id = #{id}")
    String getScoreById(String id);

    @Select("SELECT score FROM score WHERE name = #{name}")
    String getScoreByName(String name);

    @Select("SELECT AVG(score) FROM score")
    String getAverageScore();
}
