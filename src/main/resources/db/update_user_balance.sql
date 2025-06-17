-- 数据库更新脚本：添加用户余额字段
-- 创建时间：2025-06-17
-- 用途：为现有用户表添加余额字段，并为已有用户设置初始余额

USE infomanagement;

-- 检查余额字段是否已存在，如果不存在则添加
-- 使用兼容性更好的方式检查列是否存在
SET @dbname = 'infomanagement';
SET @tablename = 'users';
SET @columnname = 'balance';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (TABLE_SCHEMA = @dbname)
      AND (TABLE_NAME = @tablename)
      AND (COLUMN_NAME = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' DECIMAL(10,2) DEFAULT 0.00 COMMENT \'账户余额\'')
));

PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 为已有用户设置初始余额
-- 管理员用户设置较高余额
UPDATE users 
SET balance = 10000.00 
WHERE user_type = 'admin';

-- 普通用户设置基础余额
UPDATE users
SET balance = 100.00
WHERE user_type != 'admin';

-- 验证更新结果
SELECT id, username, nickname, user_type, balance, create_time 
FROM users 
ORDER BY user_type, create_time;

-- 显示用户余额统计
SELECT 
    user_type,
    COUNT(*) as user_count,
    MIN(balance) as min_balance,
    MAX(balance) as max_balance,
    AVG(balance) as avg_balance,
    SUM(balance) as total_balance
FROM users 
GROUP BY user_type
ORDER BY user_type;