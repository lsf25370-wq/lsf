package com.csa.assistant.controller;

import com.csa.assistant.entity.UserEntity;
import com.csa.assistant.model.*;
import com.csa.assistant.service.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class BusinessController {

    private final OrderService orderService;
    private final LogisticsService logisticsService;
    private final AccountService accountService;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final ReturnRequestService returnRequestService;
    private final PointsMallService pointsMallService;
    private final UserService userService;
    private final RedisService redisService;
    private final IntelligentAgentService agentService;

    public BusinessController(OrderService orderService,
                             LogisticsService logisticsService,
                             AccountService accountService,
                             ProductService productService,
                             PaymentService paymentService,
                             ReturnRequestService returnRequestService,
                             PointsMallService pointsMallService,
                             UserService userService,
                             RedisService redisService,
                             IntelligentAgentService agentService) {
        this.orderService = orderService;
        this.logisticsService = logisticsService;
        this.accountService = accountService;
        this.productService = productService;
        this.paymentService = paymentService;
        this.returnRequestService = returnRequestService;
        this.pointsMallService = pointsMallService;
        this.userService = userService;
        this.redisService = redisService;
        this.agentService = agentService;
    }

    private Map<String, Object> ok(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return result;
    }

    private Map<String, Object> fail(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 400);
        result.put("message", message);
        result.put("data", null);
        return result;
    }

    @GetMapping("/orders")
    public Map<String, Object> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "user001") String userId) {
        List<Order> orders;
        if (status != null && !status.isBlank()) {
            orders = orderService.getOrdersByStatus(status);
        } else if (keyword != null && !keyword.isBlank()) {
            orders = orderService.searchOrders(keyword);
        } else {
            orders = orderService.getOrdersByUserId(userId);
        }
        return ok(orders);
    }

    @GetMapping("/orders/user/{userId}")
    public Map<String, Object> getOrdersByUser(@PathVariable String userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ok(orders);
    }

    @GetMapping("/orders/{orderId}")
    public Map<String, Object> getOrderById(@PathVariable String orderId) {
        Order order = orderService.getOrderById(orderId);
        return order != null ? ok(order) : fail("订单不存在");
    }

    @PostMapping("/orders")
    public Map<String, Object> createOrder(
            @RequestParam String productId,
            @RequestParam int quantity,
            @RequestParam(defaultValue = "user001") String userId) {
        Order order = orderService.createOrder(productId, quantity, userId, productService);
        return order != null ? ok(order) : fail("创建订单失败：商品不存在或库存不足");
    }

    @PostMapping("/orders/create")
    public Map<String, Object> createOrderV2(
            @RequestParam String productId,
            @RequestParam(defaultValue = "1") int quantity,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String payMethod,
            @RequestParam(defaultValue = "user001") String userId) {
        Order order = orderService.createOrder(productId, quantity, userId, productService);
        return order != null ? ok(order) : fail("创建订单失败：商品不存在或库存不足");
    }

    @PutMapping("/orders/{orderId}/cancel")
    public Map<String, Object> cancelOrder(@PathVariable String orderId) {
        Order order = orderService.cancelOrder(orderId);
        return order != null ? ok(order) : fail("取消失败：订单状态不允许取消");
    }

    @PutMapping("/orders/{orderId}/deliver")
    public Map<String, Object> deliverOrder(@PathVariable String orderId) {
        Order order = orderService.confirmDelivered(orderId);
        return order != null ? ok(order) : fail("确认签收失败：订单状态不是'运输中'");
    }

    @GetMapping("/logistics")
    public Map<String, Object> getLogistics(@RequestParam String trackingNo) {
        LogisticsInfo logistics = logisticsService.getLogisticsByTrackingNo(trackingNo);
        return logistics != null ? ok(logistics) : fail("运单号不存在");
    }

    @GetMapping("/account")
    public Map<String, Object> getAccountInfo(
            @RequestParam(defaultValue = "user001") String userId) {
        AccountInfo account = accountService.getAccountInfo(userId);
        return account != null ? ok(account) : fail("账户不存在");
    }

    @GetMapping("/account/balance")
    public Map<String, Object> getBalance(
            @RequestParam(defaultValue = "user001") String userId) {
        return ok(accountService.getBalance(userId));
    }

    @GetMapping("/account/points")
    public Map<String, Object> getPoints(
            @RequestParam(defaultValue = "user001") String userId) {
        return ok(accountService.getPoints(userId));
    }

    @PutMapping("/users/{userId}/recharge")
    public Map<String, Object> recharge(
            @PathVariable String userId,
            @RequestParam Double amount) {
        if (amount == null || amount <= 0) {
            return fail("请输入有效的充值金额");
        }
        userService.increaseBalance(userId, amount);
        return ok("充值成功");
    }

    @GetMapping("/users/{userId}")
    public Map<String, Object> getUserById(@PathVariable String userId) {
        UserEntity user = userService.getUserById(userId);
        if (user == null) return fail("用户不存在");
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getUserId());
        data.put("username", user.getUsername());
        data.put("balance", user.getBalance());
        data.put("points", user.getPoints());
        data.put("memberLevel", user.getMemberLevel());
        data.put("email", user.getEmail());
        data.put("phone", user.getPhone());
        return ok(data);
    }

    @GetMapping("/coupons")
    public Map<String, Object> getCoupons(
            @RequestParam(defaultValue = "user001") String userId,
            @RequestParam(required = false, defaultValue = "true") boolean availableOnly) {
        List<Coupon> coupons = availableOnly
                ? accountService.getAvailableCoupons(userId)
                : accountService.getAllCoupons(userId);
        return ok(coupons);
    }

    @GetMapping("/coupons/user/{userId}")
    public Map<String, Object> getCouponsByUser(@PathVariable String userId) {
        List<Coupon> coupons = accountService.getAllCoupons(userId);
        return ok(coupons);
    }

    @PostMapping("/coupons/{couponId}/claim")
    public Map<String, Object> claimCoupon(
            @PathVariable String couponId,
            @RequestParam String userId) {
        Coupon coupon = accountService.claimCoupon(couponId, userId);
        return coupon != null ? ok(coupon) : fail("领取失败：优惠券不存在或已被领取");
    }

    @GetMapping("/coupons/all")
    public Map<String, Object> getAllAvailableCoupons() {
        List<Coupon> coupons = accountService.getAllTemplateCoupons();
        return ok(coupons);
    }

    @GetMapping("/products")
    public Map<String, Object> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        String cacheKey = "products:" + (category != null ? category : "all") + ":" + (keyword != null ? keyword : "all");
        try {
            Object cached = redisService.get(cacheKey);
            if (cached != null) {
                return ok(cached);
            }
        } catch (Exception ignored) {
        }
        List<Product> products;
        if (category != null && !category.isBlank()) {
            products = productService.getProductsByCategory(category);
        } else if (keyword != null && !keyword.isBlank()) {
            products = productService.searchProducts(keyword);
        } else {
            products = productService.getAllProducts();
        }
        try {
            redisService.set(cacheKey, products, 5, java.util.concurrent.TimeUnit.MINUTES);
        } catch (Exception ignored) {
        }
        return ok(products);
    }

    @GetMapping("/products/{productId}")
    public Map<String, Object> getProductById(@PathVariable String productId) {
        Product product = productService.getProductById(productId);
        return product != null ? ok(product) : fail("商品不存在");
    }

    @GetMapping("/payments")
    public Map<String, Object> getPayments(
            @RequestParam(defaultValue = "user001") String userId) {
        return ok(paymentService.getUserPayments(userId));
    }

    @GetMapping("/payments/user/{userId}")
    public Map<String, Object> getPaymentsByUser(@PathVariable String userId) {
        return ok(paymentService.getUserPayments(userId));
    }

    @PostMapping("/payments")
    public Map<String, Object> createPayment(
            @RequestParam String orderId,
            @RequestParam Double amount,
            @RequestParam(defaultValue = "BALANCE") String method,
            @RequestParam(defaultValue = "user001") String userId) {
        System.out.println("[BusinessController] createPayment: orderId=" + orderId + ", amount=" + amount + ", method=" + method + ", userId=" + userId);
        try {
            Payment payment = paymentService.createPayment(orderId, amount, method, userId);
            return payment != null ? ok(payment) : fail("创建支付失败");
        } catch (Exception e) {
            System.err.println("[BusinessController] createPayment exception: " + e.getMessage());
            e.printStackTrace();
            return fail("创建支付异常：" + e.getMessage());
        }
    }

    @PostMapping("/payments/create")
    public Map<String, Object> createPaymentV2(
            @RequestParam String orderId,
            @RequestParam(defaultValue = "BALANCE") String method,
            @RequestParam(defaultValue = "user001") String userId) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return fail("订单不存在");
        }
        Map<String, Object> result = createPayment(orderId, order.getAmount(), method, userId);
        if (result.get("code").equals(200)) {
            Payment payment = (Payment) result.get("data");
            if (payment != null && "PENDING".equals(payment.getStatus())) {
                Payment processed = paymentService.processPayment(payment.getPaymentId());
                if (processed != null && "SUCCESS".equals(processed.getStatus())) {
                    return ok(processed);
                }
                return fail("支付处理失败");
            }
        }
        return result;
    }

    @PutMapping("/payments/{paymentId}/pay")
    public Map<String, Object> processPayment(@PathVariable String paymentId) {
        try {
            Payment payment = paymentService.processPayment(paymentId);
            return payment != null ? ok(payment) : fail("支付失败：状态不正确或余额不足");
        } catch (Exception e) {
            e.printStackTrace();
            return fail("支付异常：" + e.getMessage());
        }
    }

    @PutMapping("/payments/{paymentId}/refund")
    public Map<String, Object> refundPayment(@PathVariable String paymentId) {
        Payment payment = paymentService.refundPayment(paymentId);
        return payment != null ? ok(payment) : fail("退款失败：状态不正确");
    }

    @GetMapping("/returns")
    public Map<String, Object> getReturns(
            @RequestParam(defaultValue = "user001") String userId) {
        return ok(returnRequestService.getUserRequests(userId));
    }

    @GetMapping("/returns/user/{userId}")
    public Map<String, Object> getReturnsByUser(@PathVariable String userId) {
        return ok(returnRequestService.getUserRequests(userId));
    }

    @PostMapping("/returns")
    public Map<String, Object> submitReturn(
            @RequestParam String orderId,
            @RequestParam String reason,
            @RequestParam(required = false) String description,
            @RequestParam Double refundAmount,
            @RequestParam(defaultValue = "user001") String userId) {
        ReturnRequest request = returnRequestService.submitReturnRequest(
                orderId, reason, description, refundAmount, userId);
        return request != null ? ok(request) : fail("提交退货申请失败：该订单已有退货申请");
    }

    @PutMapping("/returns/{requestId}/review")
    public Map<String, Object> reviewReturn(
            @PathVariable String requestId,
            @RequestParam boolean approved) {
        ReturnRequest request = returnRequestService.reviewRequest(requestId, approved);
        if (request != null && approved) {
            paymentService.refundByOrderId(request.getOrderId());
        }
        return request != null ? ok(request) : fail("审核失败：状态不正确");
    }

    @GetMapping("/points-products")
    public Map<String, Object> getPointsProducts(
            @RequestParam(required = false) String category) {
        List<PointsProduct> products = pointsMallService.getAllProducts(category);
        return ok(products);
    }

    @GetMapping("/points-products/{productId}")
    public Map<String, Object> getPointsProductById(@PathVariable String productId) {
        PointsProduct product = pointsMallService.getProductById(productId);
        return product != null ? ok(product) : fail("积分商品不存在");
    }

    @PostMapping("/points-exchange")
    public Map<String, Object> exchangePointsProduct(
            @RequestParam String productId,
            @RequestParam(required = false) String address,
            @RequestParam(defaultValue = "user001") String userId) {
        PointsExchange exchange = pointsMallService.exchangeProduct(userId, productId);
        return exchange != null ? ok(exchange) : fail("兑换失败：积分不足或库存不足");
    }

    @GetMapping("/points-exchanges")
    public Map<String, Object> getUserExchanges(
            @RequestParam(defaultValue = "user001") String userId) {
        return ok(pointsMallService.getUserExchanges(userId));
    }

    @PostMapping("/ai/chat")
    public Map<String, Object> aiChat(
            @RequestParam String message,
            @RequestParam(required = false) String userId) {
        try {
            String sessionId = "web-" + System.currentTimeMillis();
            CustomerQuery query = new CustomerQuery(sessionId, message, userId != null ? userId : "anonymous");
            ServiceResponse response = agentService.processQuery(query);
            Map<String, Object> data = new HashMap<>();
            data.put("reply", response.getAnswer());
            data.put("sessionId", sessionId);
            data.put("intent", response.getIntent() != null ? response.getIntent().name() : "UNKNOWN");
            return ok(data);
        } catch (Exception e) {
            Map<String, Object> data = new HashMap<>();
            data.put("reply", "AI服务暂不可用：" + e.getMessage());
            return ok(data);
        }
    }
}
