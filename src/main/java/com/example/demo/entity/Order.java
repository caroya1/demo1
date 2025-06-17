package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@TableName("orders")
public class Order {
  @TableId(type = IdType.AUTO)
  private Long id;

  private String orderNumber;
  private Long userId;
  private BigDecimal totalAmount;

  @TableField("status")
  private String status; // pending, paid, shipped, completed, cancelled

  private String paymentMethod;
  private String shippingAddress;
  private String remark;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
}