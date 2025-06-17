package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.LearningActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 学习活动Mapper接口
 */
@Mapper
public interface LearningActivityMapper extends BaseMapper<LearningActivity> {

  @Select("SELECT la.*, u.nickname as author FROM learning_activities la " +
      "LEFT JOIN users u ON la.author_id = u.id " +
      "WHERE la.title LIKE CONCAT('%', #{keyword}, '%') OR la.content LIKE CONCAT('%', #{keyword}, '%') " +
      "ORDER BY la.create_time DESC")
  Page<LearningActivity> selectLearningActivitiesWithAuthor(Page<LearningActivity> page,
      @Param("keyword") String keyword);
}