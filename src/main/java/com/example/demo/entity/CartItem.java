package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车实体类
 */
@Data
@TableName("cart_items")
public class CartItem {
  @TableId(type = IdType.AUTO)
  private Long id;

  private Long userId;
  private Long productId;
  private Integer quantity;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  // 非数据库字段，用于展示商品信息
  @TableField(exist = false)
  private String productName;

  @TableField(exist = false)
  private BigDecimal productPrice;

  @TableField(exist = false)
  private String productImageUrl;

  @TableField(exist = false)
  private Integer productStock;

  // 计算属性：商品总价
  @TableField(exist = false)
  private BigDecimal totalPrice;

  // 获取商品总价
  public BigDecimal getTotalPrice() {
    if (productPrice != null && quantity != null) {
      return productPrice.multiply(BigDecimal.valueOf(quantity));
    }
    return BigDecimal.ZERO;
  }
}