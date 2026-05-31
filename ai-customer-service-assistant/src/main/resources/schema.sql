CREATE DATABASE IF NOT EXISTS example_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE example_db;

DROP TABLE IF EXISTS return_requests;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS logistics;
DROP TABLE IF EXISTS coupons;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS points_products;
DROP TABLE IF EXISTS points_exchanges;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(30) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    email VARCHAR(100),
    phone VARCHAR(20),
    balance DECIMAL(10,2) DEFAULT 0,
    points INT DEFAULT 0,
    member_level VARCHAR(20),
    member_expire_date VARCHAR(20),
    create_time VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id VARCHAR(30) UNIQUE NOT NULL,
    product_name VARCHAR(200),
    category VARCHAR(50),
    description TEXT,
    price DECIMAL(10,2),
    stock INT,
    image_url VARCHAR(500),
    status VARCHAR(20) DEFAULT 'ON_SALE',
    create_time VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE orders (
    order_id VARCHAR(20) PRIMARY KEY,
    create_time VARCHAR(20),
    status VARCHAR(20),
    category VARCHAR(50),
    product_name VARCHAR(200),
    amount DECIMAL(10,2),
    receiver_name VARCHAR(50),
    receiver_address VARCHAR(500),
    tracking_no VARCHAR(50),
    logistics_company VARCHAR(50),
    user_id VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE logistics (
    tracking_no VARCHAR(50) PRIMARY KEY,
    logistics_company VARCHAR(50),
    status VARCHAR(20),
    events_json TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE accounts (
    user_id VARCHAR(50) PRIMARY KEY,
    user_name VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    balance DECIMAL(10,2),
    points INT,
    member_level VARCHAR(20),
    member_expire_date VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE coupons (
    id INT PRIMARY KEY AUTO_INCREMENT,
    coupon_id VARCHAR(20) UNIQUE,
    name VARCHAR(100),
    discount DECIMAL(10,2),
    min_amount DECIMAL(10,2),
    expire_date VARCHAR(20),
    available TINYINT(1),
    user_id VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE payments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    payment_id VARCHAR(30) UNIQUE NOT NULL,
    order_id VARCHAR(30),
    amount DECIMAL(10,2),
    payment_method VARCHAR(20),
    status VARCHAR(20) DEFAULT 'PENDING',
    create_time VARCHAR(30),
    paid_time VARCHAR(30),
    user_id VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE return_requests (
    id INT PRIMARY KEY AUTO_INCREMENT,
    request_id VARCHAR(30) UNIQUE NOT NULL,
    order_id VARCHAR(30),
    reason VARCHAR(200),
    description TEXT,
    status VARCHAR(20) DEFAULT 'SUBMITTED',
    refund_amount DECIMAL(10,2),
    create_time VARCHAR(20),
    processed_time VARCHAR(20),
    user_id VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE points_products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id VARCHAR(30) UNIQUE NOT NULL,
    product_name VARCHAR(200),
    category VARCHAR(50),
    points_cost INT,
    stock INT,
    description TEXT,
    image_url VARCHAR(500),
    status VARCHAR(20) DEFAULT 'ON_SALE',
    create_time VARCHAR(30)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE points_exchanges (
    id INT PRIMARY KEY AUTO_INCREMENT,
    exchange_id VARCHAR(30) UNIQUE NOT NULL,
    user_id VARCHAR(50),
    product_id VARCHAR(30),
    product_name VARCHAR(200),
    points_cost INT,
    status VARCHAR(20) DEFAULT '待发货',
    exchange_time VARCHAR(30),
    ship_time VARCHAR(30),
    tracking_no VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO products (product_id, product_name, category, description, price, stock, image_url, status, create_time) VALUES
('P001', 'iPhone 15 Pro Max 256GB', '电子产品', '苹果最新旗舰手机，A17 Pro芯片，钛金属设计', 8999.00, 50, '/images/iphone15.jpg', 'ON_SALE', '2024-01-01 10:00:00'),
('P002', 'MacBook Pro 14英寸 M3 Pro', '电子产品', '搭载M3 Pro芯片的专业笔记本电脑', 14999.00, 30, '/images/macbook.jpg', 'ON_SALE', '2024-01-01 10:00:00'),
('P003', 'Sony WH-1000XM5 降噪耳机', '电子产品', '索尼旗舰级无线降噪耳机，30小时续航', 2499.00, 100, '/images/sony.jpg', 'ON_SALE', '2024-01-01 10:00:00'),
('P004', 'Nike Air Jordan 1 High OG', '服装', '经典复刻篮球鞋，芝加哥配色', 1299.00, 80, '/images/aj1.jpg', 'ON_SALE', '2024-01-01 10:00:00'),
('P005', '华为Watch GT 4 Pro', '电子产品', '华为智能手表，钛合金表壳，46mm', 2688.00, 60, '/images/watch.jpg', 'ON_SALE', '2024-01-01 10:00:00'),
('P006', 'Dyson V15 Detect 无线吸尘器', '家电', '戴森旗舰无线吸尘器，激光探测微尘', 4990.00, 40, '/images/dyson.jpg', 'ON_SALE', '2024-01-01 10:00:00'),
('P007', '任天堂Switch OLED版', '电子产品', 'Nintendo Switch OLED屏幕升级版', 2099.00, 70, '/images/switch.jpg', 'ON_SALE', '2024-01-01 10:00:00'),
('P008', '三只松鼠坚果大礼包 1888g', '食品', '年货坚果礼盒，12袋精选坚果', 168.00, 200, '/images/nuts.jpg', 'ON_SALE', '2024-01-01 10:00:00'),
('P009', 'LAMER海蓝之谜面霜 60ml', '美妆', '传奇面霜，深层滋养修护', 1680.00, 45, '/images/lamer.jpg', 'ON_SALE', '2024-01-01 10:00:00'),
('P010', 'Coleman户外帐篷 4人', '户外', '速开帐篷，防风防雨，4-5人空间', 599.00, 35, '/images/tent.jpg', 'ON_SALE', '2024-01-01 10:00:00');

INSERT IGNORE INTO orders (order_id, create_time, status, category, product_name, amount, receiver_name, receiver_address, tracking_no, logistics_company, user_id) VALUES
('ORD20241201001', '2024-12-01 14:30:00', '已签收', '电子产品', 'iPhone 15 Pro 256GB 原色钛金属', 7999.00, '张三', '北京市朝阳区xxx街道xxx号', 'SF1234567890', '顺丰速运', 'user001'),
('ORD20241205002', '2024-12-05 09:15:00', '运输中', '服装', 'Nike Air Jordan 1 High OG', 1299.00, '张三', '北京市朝阳区xxx街道xxx号', 'YT9876543210', '圆通速递', 'user001'),
('ORD20241210003', '2024-12-10 16:45:00', '待支付', '图书', '深入理解计算机系统（第三版）', 129.00, '张三', '北京市朝阳区xxx街道xxx号', NULL, NULL, 'user001'),
('ORD20241212004', '2024-12-12 11:20:00', '已取消', '食品', '三只松鼠坚果大礼包', 89.90, '张三', '北京市朝阳区xxx街道xxx号', NULL, NULL, 'user001');

INSERT IGNORE INTO logistics (tracking_no, logistics_company, status, events_json) VALUES
('SF1234567890', '顺丰速运', '已签收', '[
    {"time":"2024-12-01 14:30:00","description":"快件已签收","location":"北京市朝阳区"},
    {"time":"2024-12-01 08:15:00","description":"快件正在派送中","location":"北京市朝阳区"},
    {"time":"2024-11-30 22:00:00","description":"快件从上海转运中心发出","location":"上海市"},
    {"time":"2024-11-29 18:00:00","description":"快件已揽收","location":"深圳市"}
]'),
('YT9876543210', '圆通速递', '运输中', '[
    {"time":"2024-12-06 10:30:00","description":"快件正在派送中","location":"北京市朝阳区"},
    {"time":"2024-12-05 23:45:00","description":"快件到达北京分拨中心","location":"北京市"},
    {"time":"2024-12-05 16:00:00","description":"快件从杭州转运中心发出","location":"杭州市"},
    {"time":"2024-12-05 09:15:00","description":"快件已揽收","location":"杭州市"}
]');

INSERT IGNORE INTO accounts (user_id, user_name, email, phone, balance, points, member_level, member_expire_date) VALUES
('user001', '张三', 'zhangsan@example.com', '138****8888', 1580.50, 2580, '黄金会员', '2025-12-31');

INSERT IGNORE INTO coupons (coupon_id, name, discount, min_amount, expire_date, available, user_id) VALUES
('CPN001', '新人专享券', 50.00, 200.00, '2025-12-31', 1, 'user001'),
('CPN002', '满减优惠券', 30.00, 100.00, '2025-06-30', 1, 'user001'),
('CPN003', '会员折扣券', 100.00, 500.00, '2025-12-31', 1, 'user001'),
('CPN004', '限时特惠券', 20.00, 50.00, '2025-03-01', 0, 'user001');

INSERT IGNORE INTO users (user_id, username, password, role, email, phone, balance, points, member_level, member_expire_date, create_time) VALUES
('user001', 'zhangsan', '123456', 'USER', 'zhangsan@example.com', '138****8888', 5000.00, 1000, '黄金会员', '2025-12-31', '2024-01-01 10:00:00'),
('user002', 'lisi', '123456', 'USER', 'lisi@example.com', '139****9999', 3000.00, 500, '普通会员', '2025-12-31', '2024-03-01 10:00:00'),
('admin01', 'admin', 'admin123', 'ADMIN', 'admin@example.com', '130****0000', 0.00, 0, '系统管理员', '2099-12-31', '2024-01-01 00:00:00');

INSERT IGNORE INTO points_products (product_id, product_name, category, points_cost, stock, description, image_url, status, create_time) VALUES
('PP001', '小米手环8 Pro', '电子产品', 500, 30, '智能手环，NFC版本，血氧检测', '/images/band.jpg', 'ON_SALE', '2024-01-01 10:00:00'),
('PP002', '罗技G304无线鼠标', '电子产品', 300, 50, '游戏办公两用，LIGHTSPEED无线技术', '/images/mouse.jpg', 'ON_SALE', '2024-01-01 10:00:00'),
('PP003', '星巴克中杯拿铁兑换券', '食品饮料', 150, 200, '全国门店通用，有效期90天', '/images/starbucks.jpg', 'ON_SALE', '2024-01-01 10:00:00'),
('PP004', '京东PLUS会员月卡', '会员服务', 800, 100, '京东PLUS会员1个月，免费券+运费券', '/images/jdplus.jpg', 'ON_SALE', '2024-01-01 10:00:00'),
('PP005', '电影票兑换券（2D/3D）', '娱乐生活', 200, 150, '全国2000+影院通用', '/images/movie.jpg', 'ON_SALE', '2024-01-01 10:00:00'),
('PP006', '网易云音乐季卡', '数字会员', 450, 80, '网易云音乐黑胶VIP 3个月', '/images/music.jpg', 'ON_SALE', '2024-01-01 10:00:00'),
('PP007', '定制帆布袋（联名款）', '周边礼品', 100, 300, '智联电商联名帆布袋，环保材质', '/images/bag.jpg', 'ON_SALE', '2024-01-01 10:00:00'),
('PP008', '50元无门槛优惠券', '优惠券', 250, 500, '全平台通用，无使用门槛', '/images/coupon50.jpg', 'ON_SALE', '2024-01-01 10:00:00');
