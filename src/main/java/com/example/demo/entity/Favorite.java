package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 收藏实体类
 */
@Data
@TableName("favorites")
public class Favorite {
  @TableId(type = IdType.AUTO)
  private Long id;

  private Long userId;
  private Long postId;
  private String postType;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  // 非数据库字段，用于展示详细信息
  @TableField(exist = false)
  private String title;

  @TableField(exist = false)
  private String author;

  @TableField(exist = false)
  private Integer views;
}