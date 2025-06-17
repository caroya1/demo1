package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单项实体类
 */
@Data
@TableName("order_items")
public class OrderItem {
  @TableId(type = IdType.AUTO)
  private Long id;

  private Long orderId;
  private Long productId;
  private String productName;
  private BigDecimal productPrice;
  private String productImageUrl;
  private Integer quantity;
  private BigDecimal subtotal;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;
}