package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 论坛帖子实体类
 */
@Data
@TableName("forum_posts")
public class ForumPost {
  @TableId(type = IdType.AUTO)
  private Long id;

  private String title;
  private String content;
  private Long authorId;
  private Integer views;
  private String imageUrl;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  // 非数据库字段，用于展示作者信息
  @TableField(exist = false)
  private String author;
}