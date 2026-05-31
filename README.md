# AI 客服助手系统

基于 Spring Boot 3.2 + Spring AI Alibaba 实现的智能客服助手系统，集成电商业务管理功能。

## 📋 项目介绍

本项目是一个完整的 AI 客服助手系统，包含以下核心模块：

- **智能客服模块**：基于阿里千问大模型实现智能对话、意图识别、FAQ 问答
- **电商管理后台**：订单管理、商品管理、支付管理、售后审批、积分商城、用户管理
- **Redis 缓存**：商品热点数据缓存、会话管理、排行榜
- **Docker 容器化**：一键部署，支持 MySQL + Redis + 应用服务编排

## 🛠️ 技术架构

### 技术栈

| 分类 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 21 |
| 框架 | Spring Boot | 3.2.5 |
| AI 框架 | Spring AI Alibaba | 0.8.0 |
| 数据库 | MySQL | 8.0+ |
| 缓存 | Redis | 7.0+ |
| ORM | MyBatis | 3.5.13 |
| 前端 | HTML/CSS/JS | - |
| 容器 | Docker | 24.0+ |

### 架构设计

```
┌─────────────────────────────────────────────────────────────┐
│                        前端层                              │
│  ┌──────────────────┐  ┌──────────────────┐               │
│  │   用户端页面      │  │   管理后台页面    │               │
│  └────────┬─────────┘  └────────┬─────────┘               │
└──────────┼──────────────────────┼─────────────────────────┘
           │                      │
┌──────────▼──────────────────────▼─────────────────────────┐
│                        Controller层                        │
│  BusinessController | AdminController | AuthController    │
└──────────────────────┬────────────────────────────────────┘
                       │
┌──────────────────────▼────────────────────────────────────┐
│                        Service层                           │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐            │
│  │AI服务      │ │业务服务    │ │Redis服务   │            │
│  │智能对话    │ │订单/商品   │ │缓存管理   │            │
│  │意图识别    │ │支付/积分   │ │会话管理   │            │
│  └────────────┘ └────────────┘ └────────────┘            │
└──────────────────────┬────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────┐  ┌──────────────────┐
│           MyBatis Mapper层          │  │     Redis层      │
└──────────────────────┬──────────────┘  └──────────────────┘
                       │
┌──────────────────────▼──────────────┐
│           MySQL 数据库              │
│  users | products | orders | ...   │
└─────────────────────────────────────┘
```

## 🗄️ 数据库设计

### 核心数据表

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `users` | 用户表 | user_id, username, role, balance, points |
| `products` | 商品表 | product_id, name, price, stock, status |
| `orders` | 订单表 | order_id, user_id, amount, status, create_time |
| `payments` | 支付记录表 | payment_id, order_id, amount, status |
| `return_requests` | 售后申请表 | request_id, order_id, reason, status |
| `points_products` | 积分商品表 | product_id, name, points_required |
| `coupons` | 优惠券表 | coupon_id, user_id, discount, expire_time |

### ER 关系图

```
用户(users) 1:n 订单(orders)
用户(users) 1:n 优惠券(coupons)
订单(orders) 1:1 支付(payments)
订单(orders) 1:1 售后(return_requests)
用户(users) n:m 积分商品(points_products)
```

## 🚀 部署说明

### 环境要求

- JDK 21+
- MySQL 8.0+
- Redis 7.0+
- Maven 3.8+
- Docker 24.0+ (可选)

### 1. 手动部署

```bash
# 1. 克隆项目
git clone https://github.com/lsf25370-wq/lsf.git

# 2. 进入项目目录
cd SpringAiAlibaba-guiguv1

# 3. 配置数据库连接 (application.properties)
# 修改数据库 URL、用户名、密码

# 4. 构建项目
mvn clean package -DskipTests

# 5. 运行项目
java -jar ai-customer-service-assistant/target/ai-customer-service-assistant-1.0.0.jar
```

### 2. Docker Compose 部署

```bash
# 使用 docker-compose 一键启动
cd ai-customer-service-assistant
docker-compose up -d
```

### 3. 环境变量配置

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `SPRING_DATASOURCE_URL` | MySQL连接地址 | jdbc:mysql://localhost:3306/example_db |
| `SPRING_DATASOURCE_USERNAME` | MySQL用户名 | root |
| `SPRING_DATASOURCE_PASSWORD` | MySQL密码 | 123456 |
| `SPRING_DATA_REDIS_HOST` | Redis地址 | localhost |
| `SPRING_DATA_REDIS_PORT` | Redis端口 | 6379 |
| `aliQwen-api` | 阿里云API Key | - |

## 📁 项目结构

```
SpringAiAlibaba-guiguv1/
├── ai-customer-service-assistant/    # 主应用模块
│   ├── src/main/java/com/csa/assistant/
│   │   ├── controller/               # REST API 控制器
│   │   ├── service/                  # 业务服务层
│   │   ├── mapper/                   # MyBatis 映射
│   │   ├── entity/                   # 数据库实体
│   │   ├── model/                    # 数据模型
│   │   ├── config/                   # 配置类
│   │   └── CustomerServiceAssistantApplication.java
│   ├── src/main/resources/
│   │   ├── mapper/                   # MyBatis XML
│   │   ├── static/                   # 前端静态资源
│   │   ├── application.properties    # 应用配置
│   │   └── schema.sql                # 数据库初始化
│   ├── Dockerfile                    # Docker 构建文件
│   ├── docker-compose.yml            # Docker Compose 配置
│   └── pom.xml
├── SSA-01HelloWorld/                 # Spring AI 入门示例
├── SSA-02OllamaChat/                 # Ollama 聊天示例
├── SSA-03ChatClent/                  # 聊天客户端示例
├── SSA-04StreamingOutput/            # 流式输出示例
├── SSA-05Prompt/                     # Prompt 示例
├── SSA-06PromptTemplate/             # Prompt模板示例
├── SSA-07StructuredOutput/           # 结构化输出示例
├── SSA-08Persistent/                 # 持久化示例
└── pom.xml                           # 父工程配置
```

## 🔧 功能模块

### 管理后台功能

| 模块 | 功能 |
|------|------|
| 数据看板 | 订单统计、销售统计、用户统计 |
| 订单管理 | 订单列表、状态修改、订单详情 |
| 支付管理 | 支付记录、支付状态查询 |
| 售后审批 | 售后申请列表、审批操作 |
| 商品管理 | 商品CRUD、上下架管理 |
| 积分商城 | 积分商品管理、兑换记录 |
| 用户管理 | 用户列表、权限管理 |

### 用户端功能

| 模块 | 功能 |
|------|------|
| 商品浏览 | 商品列表、商品详情 |
| 订单管理 | 创建订单、订单查询 |
| 积分商城 | 积分兑换 |
| AI客服 | 智能对话咨询 |
| 个人中心 | 余额、积分、优惠券 |

## 📱 访问地址

- **用户端首页**: `http://localhost:8080/index.html`
- **管理后台**: `http://localhost:8080/admin.html`
- **API 文档**: `http://localhost:8080/api/`

## 🔑 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 普通用户 | user | user123 |

## 📝 开发说明

### 启动方式

```bash
# 开发模式运行
mvn spring-boot:run -pl ai-customer-service-assistant

# 打包构建
mvn clean package -DskipTests
```

### 代码规范

- Java 代码遵循阿里巴巴编码规范
- 前端代码使用 ES6+ 标准
- 提交信息使用约定式提交规范

## 📄 许可证

MIT License

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

---

**项目已部署** 🚀

- GitHub: [https://github.com/lsf25370-wq/lsf](https://github.com/lsf25370-wq/lsf)
- Docker Hub: [https://hub.docker.com/r/lsf25370/csa-app](https://hub.docker.com/r/lsf25370/csa-app)