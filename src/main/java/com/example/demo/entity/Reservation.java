package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 预约实体类
 */
@Data
@TableName("reservations")
public class Reservation {
  @TableId(type = IdType.AUTO)
  private Long id;

  private Long userId;
  private Long activityId;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  // 非数据库字段，用于展示活动信息
  @TableField(exist = false)
  private String title;

  @TableField(exist = false)
  private String author;

  @TableField(exist = false)
  private Integer reservedCount;

  @TableField(exist = false)
  private String status;

  @TableField(exist = false)
  private LocalDateTime activityCreateTime;
}