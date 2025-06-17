package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值记录实体类
 */
@Data
@TableName("recharge_records")
public class RechargeRecord {
  @TableId(type = IdType.AUTO)
  private Long id;

  private Long userId;
  private BigDecimal amount;
  private String paymentMethod; // 支付方式：alipay, wechat, bank_card
  private String status; // 状态：pending, success, failed
  private String transactionId; // 交易ID
  private String remark; // 备注

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
}