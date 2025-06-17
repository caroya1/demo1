package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.ForumPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 论坛帖子Mapper接口
 */
@Mapper
public interface ForumPostMapper extends BaseMapper<ForumPost> {

  @Select("SELECT fp.*, u.nickname as author FROM forum_posts fp " +
      "LEFT JOIN users u ON fp.author_id = u.id " +
      "WHERE fp.title LIKE CONCAT('%', #{keyword}, '%') OR fp.content LIKE CONCAT('%', #{keyword}, '%') " +
      "ORDER BY fp.create_time DESC")
  Page<ForumPost> selectForumPostsWithAuthor(Page<ForumPost> page, @Param("keyword") String keyword);
}