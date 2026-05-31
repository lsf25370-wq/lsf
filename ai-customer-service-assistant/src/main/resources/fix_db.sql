-- 修复数据库表字段问题
USE example_db;

-- 1. 扩展 create_time 和 paid_time 字段长度（从 20 到 30）
ALTER TABLE payments MODIFY COLUMN create_time VARCHAR(30);
ALTER TABLE payments MODIFY COLUMN paid_time VARCHAR(30);

-- 2. 扩展 points_products 表的 create_time 字段
ALTER TABLE points_products MODIFY COLUMN create_time VARCHAR(30);

-- 3. 扩展 points_exchanges 表的 exchange_time 和 ship_time 字段
ALTER TABLE points_exchanges MODIFY COLUMN exchange_time VARCHAR(30);
ALTER TABLE points_exchanges MODIFY COLUMN ship_time VARCHAR(30);

-- 4. 修复用户积分（lizi 积分太少，充值到 5000）
UPDATE users SET points = 5000 WHERE user_id = 'user002';
UPDATE users SET points = 5000 WHERE username = 'lizi';

-- 验证修复结果
SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'example_db' AND COLUMN_NAME LIKE '%time%' 
AND TABLE_NAME IN ('payments', 'points_products', 'points_exchanges');
