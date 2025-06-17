package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

  /**
   * 获取用户发布的帖子
   */
  @Select("SELECT fp.id, fp.title, fp.content, fp.views, fp.create_time as createTime, " +
      "'已发布' as status " +
      "FROM forum_posts fp " +
      "WHERE fp.author_id = #{userId} " +
      "ORDER BY fp.create_time DESC")
  List<Map<String, Object>> selectUserPosts(@Param("userId") Long userId);

  /**
   * 获取用户的预约记录
   */
  @Select("SELECT la.id, la.title, u.nickname as author, la.create_time as createTime, " +
      "la.reserved_count as reservedCount, r.create_time as reservationTime " +
      "FROM reservations r " +
      "JOIN learning_activities la ON r.activity_id = la.id " +
      "JOIN users u ON la.author_id = u.id " +
      "WHERE r.user_id = #{userId} " +
      "ORDER BY r.create_time DESC")
  List<Map<String, Object>> selectUserReservations(@Param("userId") Long userId);
}