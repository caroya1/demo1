-- 创建充值记录表
CREATE TABLE IF NOT EXISTS recharge_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL COMMENT '支付方式：alipay, wechat, bank_card',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending, success, failed',
    transaction_id VARCHAR(100) UNIQUE COMMENT '交易ID',
    remark VARCHAR(255) COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充值记录表';