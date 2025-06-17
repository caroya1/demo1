package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 收藏Mapper接口
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

  @Select("SELECT f.*, " +
      "CASE " +
      "  WHEN f.post_type = 'forum' THEN (SELECT title FROM forum_posts WHERE id = f.post_id) " +
      "  WHEN f.post_type = 'learning' THEN (SELECT title FROM learning_activities WHERE id = f.post_id) " +
      "END as title, " +
      "CASE " +
      "  WHEN f.post_type = 'forum' THEN (SELECT u.nickname FROM forum_posts fp LEFT JOIN users u ON fp.author_id = u.id WHERE fp.id = f.post_id) "
      +
      "  WHEN f.post_type = 'learning' THEN (SELECT u.nickname FROM learning_activities la LEFT JOIN users u ON la.author_id = u.id WHERE la.id = f.post_id) "
      +
      "END as author, " +
      "CASE " +
      "  WHEN f.post_type = 'forum' THEN (SELECT views FROM forum_posts WHERE id = f.post_id) " +
      "  WHEN f.post_type = 'learning' THEN (SELECT views FROM learning_activities WHERE id = f.post_id) " +
      "END as views " +
      "FROM favorites f WHERE f.user_id = #{userId} ORDER BY f.create_time DESC")
  List<Favorite> selectUserFavoritesWithDetails(@Param("userId") Long userId);
}