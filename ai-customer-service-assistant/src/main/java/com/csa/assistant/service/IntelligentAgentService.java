package com.csa.assistant.service;

import com.csa.assistant.model.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IntelligentAgentService {

    private final ChatClient chatClient;
    private final KnowledgeBaseService knowledgeBaseService;
    private final IntentRecognitionService intentRecognitionService;
    private final OrderService orderService;
    private final LogisticsService logisticsService;
    private final AccountService accountService;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final ReturnRequestService returnRequestService;

    private static final Pattern ORDER_ID_PATTERN = Pattern.compile("(ORD\\d{10,12})|(RET\\d{10,15})");
    private static final Pattern TRACKING_NO_PATTERN = Pattern.compile("([A-Z]{2}\\d{9,12})");

    public IntelligentAgentService(ChatClient customerServiceChatClient,
                                   KnowledgeBaseService knowledgeBaseService,
                                   IntentRecognitionService intentRecognitionService,
                                   OrderService orderService,
                                   LogisticsService logisticsService,
                                   AccountService accountService,
                                   ProductService productService,
                                   PaymentService paymentService,
                                   ReturnRequestService returnRequestService) {
        this.chatClient = customerServiceChatClient;
        this.knowledgeBaseService = knowledgeBaseService;
        this.intentRecognitionService = intentRecognitionService;
        this.orderService = orderService;
        this.logisticsService = logisticsService;
        this.accountService = accountService;
        this.productService = productService;
        this.paymentService = paymentService;
        this.returnRequestService = returnRequestService;
    }

    public ServiceResponse processQuery(CustomerQuery query) {
        IntentType intent = intentRecognitionService.recognizeIntent(query.getQuery());
        query.setDetectedIntent(intent);

        String businessData = buildBusinessContext(query.getQuery(), intent, query.getUserId());
        String knowledgeContext = knowledgeBaseService.buildKnowledgeContext(query.getQuery());

        String systemPrompt = buildSystemPrompt(intent, knowledgeContext, businessData);

        String answer = chatClient.prompt()
                .system(systemPrompt)
                .user(query.getQuery())
                .call()
                .content();

        return new ServiceResponse(query.getSessionId(), intent, answer, 0.85);
    }

    public Flux<String> processQueryStream(CustomerQuery query) {
        IntentType intent = intentRecognitionService.recognizeIntent(query.getQuery());
        query.setDetectedIntent(intent);

        String businessData = buildBusinessContext(query.getQuery(), intent, query.getUserId());
        String knowledgeContext = knowledgeBaseService.buildKnowledgeContext(query.getQuery());

        String systemPrompt = buildSystemPrompt(intent, knowledgeContext, businessData);

        return chatClient.prompt()
                .system(systemPrompt)
                .user(query.getQuery())
                .stream()
                .content();
    }

    private String buildBusinessContext(String query, IntentType intent, String userId) {
        StringBuilder ctx = new StringBuilder();

        switch (intent) {
            case PRODUCT_INQUIRY:
                ctx.append(productService.getProductRecommendation(query));
                ctx.append("\n");
                List<Product> searchResults = productService.searchProducts(query);
                if (!searchResults.isEmpty()) {
                    ctx.append("\n## 搜索结果\n");
                    for (Product p : searchResults) {
                        ctx.append("- ").append(p.getName())
                            .append(" | ¥").append(p.getPrice())
                            .append(" | 库存:").append(p.getStock()).append("\n");
                    }
                }
                ctx.append(accountService.getAccountInfo(userId) != null
                        ? "\n" + buildAccountBrief(userId) : "");
                break;

            case ORDER_ISSUE:
                ctx.append(buildOrderContext(query, userId));
                break;

            case COMPLAINT:
                ctx.append(buildOrderContext(query, userId));
                ctx.append(returnRequestService.getReturnSummary(userId));
                break;

            case TECHNICAL_SUPPORT:
                ctx.append(buildAccountBrief(userId));
                ctx.append("\n");
                ctx.append(paymentService.getPaymentSummary(userId));
                break;

            case ACCOUNT_HELP:
                ctx.append(buildFullAccountInfo(userId));
                ctx.append("\n");
                ctx.append(paymentService.getPaymentSummary(userId));
                break;

            default:
                ctx.append(productService.getProductRecommendation(query));
                break;
        }

        String trackingNo = extractTrackingNo(query);
        if (trackingNo != null) {
            LogisticsInfo logistics = logisticsService.getLogisticsByTrackingNo(trackingNo);
            if (logistics != null) {
                ctx.append("\n").append(buildLogisticsDetail(logistics));
            }
        }

        return ctx.toString();
    }

    private String buildOrderContext(String query, String userId) {
        StringBuilder ctx = new StringBuilder();
        String orderId = extractOrderId(query);
        if (orderId != null) {
            Order order = orderService.getOrderById(orderId);
            if (order != null) {
                ctx.append(buildOrderDetail(order));
                Payment payment = paymentService.getPaymentByOrderId(order.getOrderId());
                if (payment != null) {
                    ctx.append("\n").append(buildPaymentDetail(payment));
                }
            } else {
                ctx.append("未找到订单：").append(orderId);
            }
        } else {
            List<Order> orders = orderService.getOrdersByUserId(userId);
            ctx.append("## 订单列表（共").append(orders.size()).append("条）\n");
            for (Order o : orders) {
                ctx.append("- **").append(o.getOrderId()).append("** | ")
                    .append(o.getProductName()).append(" | ")
                    .append(o.getStatus()).append(" | ¥")
                    .append(String.format("%.2f", o.getAmount())).append("\n");
            }
        }
        return ctx.toString();
    }

    private String buildOrderDetail(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 订单详情\n");
        sb.append("- 订单号：").append(order.getOrderId()).append("\n");
        sb.append("- 商品：").append(order.getProductName()).append("\n");
        sb.append("- 金额：¥").append(String.format("%.2f", order.getAmount())).append("\n");
        sb.append("- 状态：").append(order.getStatus()).append("\n");
        sb.append("- 创建时间：").append(order.getCreateTime()).append("\n");
        if (order.getTrackingNo() != null) {
            sb.append("- 运单号：").append(order.getTrackingNo())
              .append("（").append(order.getLogisticsCompany()).append("）\n");
        }
        return sb.toString();
    }

    private String buildPaymentDetail(Payment payment) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 支付信息\n");
        sb.append("- 支付单号：").append(payment.getPaymentId()).append("\n");
        sb.append("- 支付金额：¥").append(String.format("%.2f", payment.getAmount())).append("\n");
        sb.append("- 支付方式：").append(payment.getMethod()).append("\n");
        sb.append("- 支付状态：").append(payment.getStatus()).append("\n");
        return sb.toString();
    }

    private String buildLogisticsDetail(LogisticsInfo logistics) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 物流详情\n");
        sb.append("- 运单号：").append(logistics.getTrackingNo()).append("\n");
        sb.append("- 快递公司：").append(logistics.getLogisticsCompany()).append("\n");
        sb.append("- 状态：").append(logistics.getStatus()).append("\n");
        sb.append("### 物流轨迹\n");
        if (logistics.getTrackingEvents() != null) {
            for (TrackingEvent e : logistics.getTrackingEvents()) {
                sb.append("- ").append(e.getTime()).append(" | ")
                  .append(e.getLocation()).append(" | ")
                  .append(e.getDescription()).append("\n");
            }
        }
        return sb.toString();
    }

    private String buildAccountBrief(String userId) {
        AccountInfo account = accountService.getAccountInfo(userId);
        if (account == null) return "";
        return "## 账户信息\n- 用户：" + account.getUserName()
                + "\n- 余额：¥" + String.format("%.2f", account.getBalance())
                + "\n- 积分：" + account.getPoints()
                + "\n- 会员等级：" + account.getMemberLevel();
    }

    private String buildFullAccountInfo(String userId) {
        StringBuilder sb = new StringBuilder(buildAccountBrief(userId));
        List<Coupon> coupons = accountService.getAvailableCoupons(userId);
        if (!coupons.isEmpty()) {
            sb.append("\n\n## 可用优惠券（").append(coupons.size()).append("张）\n");
            for (Coupon c : coupons) {
                sb.append("- ").append(c.getName())
                  .append("：满").append(c.getMinAmount())
                  .append("减").append(c.getDiscount())
                  .append(" 有效期至").append(c.getExpireDate()).append("\n");
            }
        }
        return sb.toString();
    }

    private String extractOrderId(String query) {
        Matcher matcher = ORDER_ID_PATTERN.matcher(query);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private String extractTrackingNo(String query) {
        Matcher matcher = TRACKING_NO_PATTERN.matcher(query);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private String buildSystemPrompt(IntentType intent, String knowledgeContext, String businessData) {
        return """
                你是一个专业的企业级智能客服助手，服务于「智联电商」平台。你的核心能力是基于真实业务数据回答客户问题。
                
                ## 当前用户意图
                %s（%s）
                
                ## 业务数据（从数据库中实时获取，请优先使用）
                %s
                
                ## 知识库参考
                %s
                
                ## 回答规则
                1. 优先使用"业务数据"中的真实信息回答，不要编造信息
                2. 如果用户询问订单，先展示订单列表或详情，再给出建议
                3. 如果用户询问物流，根据运单号查询轨迹
                4. 如果用户询问商品，展示热销商品列表
                5. 如果用户投诉，先安抚情绪再提供解决方案
                6. 需要人工处理时，告知客服热线 400-800-8888
                7. 回答简洁专业，用自然语言，不要使用 JSON 或代码格式
                8. 数据处理：金额保留2位小数，数量为正整数
                """
                .formatted(intent.getCode(), intent.getDescription(),
                        businessData.isEmpty() ? "暂无相关业务数据" : businessData,
                        knowledgeContext);
    }
}
