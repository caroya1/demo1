package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学习活动实体类
 */
@Data
@TableName("learning_activities")
public class LearningActivity {
  @TableId(type = IdType.AUTO)
  private Long id;

  private String title;
  private String content;
  private Long authorId;
  private Integer views;
  private String imageUrl;
  private Integer reservedCount;
  private Integer maxCapacity;
  private String status;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  // 非数据库字段，用于展示作者信息
  @TableField(exist = false)
  private String author;
}