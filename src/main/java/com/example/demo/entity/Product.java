package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
@Data
@TableName("products")
public class Product {
  @TableId(type = IdType.AUTO)
  private Long id;

  private String name;
  private String description;
  private BigDecimal price;
  private BigDecimal originalPrice;
  private Integer stock;
  private String category;
  private String imageUrl;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
}