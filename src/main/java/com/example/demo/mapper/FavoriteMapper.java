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
      "  WHEN f.post_type = 'forum' THEN fp.title " +
      "  WHEN f.post_type = 'learning' THEN la.title " +
      "  ELSE '未知标题' " +
      "END as title, " +
      "CASE " +
      "  WHEN f.post_type = 'forum' THEN u1.nickname " +
      "  WHEN f.post_type = 'learning' THEN u2.nickname " +
      "  ELSE '未知作者' " +
      "END as author, " +
      "CASE " +
      "  WHEN f.post_type = 'forum' THEN fp.views " +
      "  WHEN f.post_type = 'learning' THEN la.views " +
      "  ELSE 0 " +
      "END as views, " +
      "CASE " +
      "  WHEN f.post_type = 'forum' THEN fp.create_time " +
      "  WHEN f.post_type = 'learning' THEN la.create_time " +
      "  ELSE f.create_time " +
      "END as post_create_time " +
      "FROM favorites f " +
      "LEFT JOIN forum_posts fp ON f.post_type = 'forum' AND f.post_id = fp.id " +
      "LEFT JOIN learning_activities la ON f.post_type = 'learning' AND f.post_id = la.id " +
      "LEFT JOIN users u1 ON fp.author_id = u1.id " +
      "LEFT JOIN users u2 ON la.author_id = u2.id " +
      "WHERE f.user_id = #{userId} " +
      "ORDER BY f.create_time DESC")
  List<Favorite> selectUserFavoritesWithDetails(@Param("userId") Long userId);
}