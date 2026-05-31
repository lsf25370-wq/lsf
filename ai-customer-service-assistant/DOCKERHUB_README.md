# 智联电商 AI 客服系统

基于 Spring Boot 3.2 + Spring AI Alibaba + Redis + MySQL 的企业级智能电商平台

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.2.5 | 基础框架 |
| Spring AI Alibaba | 1.0.0.4 | AI 智能客服（千问大模型） |
| MyBatis | 3.0.3 | 数据库 ORM |
| MySQL | 8.0 | 持久化存储 |
| Spring Data Redis | 3.2.5 | 缓存加速 |
| Docker | - | 容器化部署 |

## 功能模块

### 用户端
- 商品浏览、搜索、分类筛选
- 下单购买（余额/积分/混合支付）
- 订单管理、物流跟踪
- 售后申请、退货退款
- 积分商城兑换
- 优惠券领取
- 余额充值
- AI 智能客服

### 管理后台
- 数据看板（ECharts 可视化 + Redis 状态监控）
- 订单管理（发货、签收）
- 支付管理、售后审批
- 商品上下架、CRUD
- 积分商城管理
- 用户管理

## 快速启动

### 前置条件
- MySQL 8.0+ （创建数据库 example_db）
- 可选的阿里云 DashScope API Key（AI 客服功能需要）

### 数据库初始化
```sql
-- 连接 MySQL 执行项目中的 schema.sql
source schema.sql
```

### Docker 运行
```bash
docker run -d --name csa-app -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://宿主机IP:3306/example_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai" \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=你的密码 \
  -e SPRING_DATA_REDIS_HOST=Redis地址 \
  -e SPRING_DATA_REDIS_PORT=6379 \
  lsf25370/csa-app:1.0
```

### 访问
- 用户端：http://localhost:8080
- 管理员：http://localhost:8080/admin.html
- 测试账号：zhangsan / 123456（用户）｜ admin / admin123（管理员）

## API 文档

| 路径 | 方法 | 说明 |
|------|------|------|
| /api/v1/products | GET | 商品列表（支持分类/关键词，Redis 缓存） |
| /api/v1/orders | GET/POST | 订单管理 |
| /api/v1/payments | GET/POST | 支付管理 |
| /api/v1/users/{id}/recharge | PUT | 余额充值 |
| /api/v1/coupons | GET/POST | 优惠券管理 |
| /api/v1/ai/chat | POST | AI 客服对话 |
| /api/v1/admin/dashboard | GET | 数据看板 |
| /api/v1/admin/redis/status | GET | Redis 连接状态 |

## 项目结构
```
src/main/java/com/csa/assistant/
├── config/          # Redis、AI 配置
├── controller/      # REST 接口
├── service/         # 业务逻辑 + Redis 缓存服务
├── mapper/          # MyBatis 数据访问
├── entity/          # 数据实体
└── model/           # 业务模型
src/main/resources/
├── application.properties
├── schema.sql       # 建表 + 初始数据
└── static/          # 前端页面
```
