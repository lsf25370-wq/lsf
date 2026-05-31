package com.csa.assistant.controller;

import com.csa.assistant.entity.UserEntity;
import com.csa.assistant.model.*;
import com.csa.assistant.service.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final OrderService orderService;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final ReturnRequestService returnRequestService;
    private final UserService userService;
    private final PointsMallService pointsMallService;
    private final RedisService redisService;

    public AdminController(OrderService orderService, ProductService productService,
                          PaymentService paymentService, ReturnRequestService returnRequestService,
                          UserService userService, PointsMallService pointsMallService,
                          RedisService redisService) {
        this.orderService = orderService;
        this.productService = productService;
        this.paymentService = paymentService;
        this.returnRequestService = returnRequestService;
        this.userService = userService;
        this.pointsMallService = pointsMallService;
        this.redisService = redisService;
    }

    private Map<String, Object> ok(Object data) {
        Map<String, Object> r = new HashMap<>(); r.put("code", 200); r.put("message", "success"); r.put("data", data); return r;
    }
    private Map<String, Object> fail(String msg) {
        Map<String, Object> r = new HashMap<>(); r.put("code", 400); r.put("message", msg); r.put("data", null); return r;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password) {
        UserEntity user = userService.login(username, password);
        if (user == null) {
            return fail("用户名或密码错误");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("token", user.getUserId());
        data.put("userId", user.getUserId());
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        data.put("balance", user.getBalance());
        data.put("points", user.getPoints());
        return ok(data);
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        List<Order> orders = orderService.getAllOrders();
        List<ReturnRequest> returns = returnRequestService.getAllRequests();
        List<UserEntity> users = userService.getAllUsers();
        List<Payment> payments = paymentService.getAllPayments();

        BigDecimal totalSales = orders.stream()
                .filter(o -> "已签收".equals(o.getStatus()) || "已支付".equals(o.getStatus()))
                .map(o -> BigDecimal.valueOf(o.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalRefund = returns.stream()
                .filter(r -> "APPROVED".equals(r.getStatus()) || "COMPLETED".equals(r.getStatus()))
                .map(r -> r.getRefundAmount() != null ? BigDecimal.valueOf(r.getRefundAmount()) : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", orders.size());
        stats.put("pendingOrders", orders.stream().filter(o -> "待支付".equals(o.getStatus())).count());
        stats.put("shippingOrders", orders.stream().filter(o -> "运输中".equals(o.getStatus())).count());
        stats.put("completedOrders", orders.stream().filter(o -> "已签收".equals(o.getStatus())).count());
        stats.put("cancelledOrders", orders.stream().filter(o -> "已取消".equals(o.getStatus())).count());
        stats.put("pendingReturns", returns.stream().filter(r -> "SUBMITTED".equals(r.getStatus())).count());
        stats.put("totalProducts", productService.getAllProductsWithStatus().size());
        stats.put("totalUsers", users.size());
        stats.put("totalSales", totalSales);
        stats.put("totalRefund", totalRefund);
        stats.put("totalPayments", payments.size());
        stats.put("successPayments", payments.stream().filter(p -> "SUCCESS".equals(p.getStatus())).count());
        return ok(stats);
    }

    @GetMapping("/users")
    public Map<String, Object> getAllUsers() {
        return ok(userService.getAllUsers());
    }

    @PutMapping("/users/{userId}/balance")
    public Map<String, Object> updateUserBalance(@PathVariable String userId, @RequestParam Double balance) {
        userService.updateBalanceDirect(userId, balance);
        return ok("余额已更新");
    }

    @PutMapping("/users/{userId}/points")
    public Map<String, Object> updateUserPoints(@PathVariable String userId, @RequestParam Integer points) {
        userService.updatePointsDirect(userId, points);
        return ok("积分已更新");
    }

    @PutMapping("/users/{userId}/level")
    public Map<String, Object> updateUserLevel(@PathVariable String userId, @RequestParam String level) {
        UserEntity user = userService.getUserById(userId);
        if (user != null) {
            user.setMemberLevel(level);
        }
        return ok("会员等级已更新");
    }

    @DeleteMapping("/users/{userId}")
    public Map<String, Object> deleteUser(@PathVariable String userId) {
        UserEntity user = userService.getUserById(userId);
        if (user != null && !"ADMIN".equals(user.getRole())) {
            boolean success = userService.deleteUser(userId);
            return success ? ok("用户已删除") : fail("删除失败");
        }
        return fail("删除失败：不能删除管理员账号");
    }

    @PostMapping("/users")
    public Map<String, Object> createUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(defaultValue = "") String email,
            @RequestParam(defaultValue = "") String phone,
            @RequestParam(defaultValue = "USER") String role) {
        int result = userService.register(username, password, email, phone, role);
        if (result > 0) return ok("用户创建成功");
        return fail("创建失败，用户名可能已存在");
    }

    @GetMapping("/products")
    public Map<String, Object> getAllProducts() {
        return ok(productService.getAllProductsWithStatus());
    }

    @PostMapping("/products")
    public Map<String, Object> createProduct(
            @RequestParam String productName,
            @RequestParam String category,
            @RequestParam Double price,
            @RequestParam Integer stock,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String imageUrl) {
        Product product = productService.createProduct(productName, category, price, stock, description);
        return product != null ? ok(product) : fail("创建商品失败");
    }

    @PutMapping("/products/{productId}")
    public Map<String, Object> updateProduct(
            @PathVariable String productId,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) Integer stock,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String imageUrl) {
        Product product = productService.updateProduct(productId, productName, category, price, stock, description, status);
        return product != null ? ok(product) : fail("更新商品失败");
    }

    @PutMapping("/products/{productId}/toggle")
    public Map<String, Object> toggleProductStatus(@PathVariable String productId) {
        productService.toggleProductStatus(productId);
        return ok("状态已切换");
    }

    @DeleteMapping("/products/{productId}")
    public Map<String, Object> deleteProduct(@PathVariable String productId) {
        boolean success = productService.deleteProduct(productId);
        return success ? ok("商品已删除") : fail("删除失败");
    }

    @GetMapping("/orders")
    public Map<String, Object> getAllOrders() {
        return ok(orderService.getAllOrders());
    }

    @PutMapping("/orders/{orderId}/ship")
    public Map<String, Object> shipOrder(@PathVariable String orderId,
                                         @RequestParam String trackingNo,
                                         @RequestParam String logisticsCompany) {
        Order order = orderService.shipOrder(orderId, trackingNo, logisticsCompany);
        return order != null ? ok(order) : fail("发货失败：订单状态不是'已支付'");
    }

    @PutMapping("/orders/{orderId}/deliver")
    public Map<String, Object> deliverOrder(@PathVariable String orderId) {
        Order order = orderService.confirmDelivered(orderId);
        return order != null ? ok(order) : fail("确认签收失败：订单状态不是'运输中'");
    }

    @PutMapping("/orders/{orderId}/status")
    public Map<String, Object> updateOrderStatus(@PathVariable String orderId, @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
        return ok("状态已更新");
    }

    @GetMapping("/returns")
    public Map<String, Object> getAllReturns() {
        return ok(returnRequestService.getAllRequests());
    }

    @PutMapping("/returns/{requestId}/approve")
    public Map<String, Object> approveReturn(@PathVariable String requestId) {
        ReturnRequest request = returnRequestService.reviewRequest(requestId, true);
        if (request != null) {
            paymentService.refundByOrderId(request.getOrderId());
            return ok(request);
        }
        return fail("审核失败");
    }

    @PutMapping("/returns/{requestId}/reject")
    public Map<String, Object> rejectReturn(@PathVariable String requestId) {
        ReturnRequest request = returnRequestService.reviewRequest(requestId, false);
        return request != null ? ok(request) : fail("审核失败");
    }

    @PutMapping("/returns/{requestId}/complete")
    public Map<String, Object> completeReturn(@PathVariable String requestId) {
        ReturnRequest request = returnRequestService.completeReturn(requestId);
        return request != null ? ok(request) : fail("操作失败");
    }

    @GetMapping("/payments")
    public Map<String, Object> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        BigDecimal totalAmount = payments.stream()
                .filter(p -> "SUCCESS".equals(p.getStatus()))
                .map(p -> BigDecimal.valueOf(p.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Object> result = new HashMap<>();
        result.put("list", payments);
        result.put("totalAmount", totalAmount);
        result.put("totalCount", payments.size());
        return ok(result);
    }

    @GetMapping("/account")
    public Map<String, Object> getAccount() {
        return ok(userService.getUserById("user001"));
    }

    @GetMapping("/points-exchanges")
    public Map<String, Object> getAllExchanges() {
        return ok(pointsMallService.getAllExchanges());
    }

    @PutMapping("/points-exchanges/{exchangeId}/ship")
    public Map<String, Object> shipExchange(
            @PathVariable String exchangeId,
            @RequestParam String trackingNo) {
        PointsExchange exchange = pointsMallService.shipExchange(exchangeId, trackingNo);
        return exchange != null ? ok(exchange) : fail("发货失败");
    }

    @GetMapping("/points-products")
    public Map<String, Object> getAllPointsProducts() {
        return ok(pointsMallService.getAllProducts(null));
    }

    @PostMapping("/points-products")
    public Map<String, Object> createPointsProduct(
            @RequestParam String productName,
            @RequestParam String category,
            @RequestParam Integer pointsCost,
            @RequestParam Integer stock,
            @RequestParam(required = false) String description) {
        PointsProduct product = pointsMallService.createProduct(productName, category, pointsCost, stock, description);
        return product != null ? ok(product) : fail("创建积分商品失败");
    }

    @PutMapping("/points-products/{productId}")
    public Map<String, Object> updatePointsProduct(
            @PathVariable String productId,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Integer pointsCost,
            @RequestParam(required = false) Integer stock,
            @RequestParam(required = false) String status) {
        PointsProduct product = pointsMallService.updateProduct(productId, productName, pointsCost, stock, status);
        return product != null ? ok(product) : fail("更新积分商品失败");
    }

    @DeleteMapping("/points-products/{productId}")
    public Map<String, Object> deletePointsProduct(@PathVariable String productId) {
        boolean success = pointsMallService.deleteProduct(productId);
        return success ? ok("积分商品已删除") : fail("删除失败");
    }

    @GetMapping("/redis/status")
    public Map<String, Object> redisStatus() {
        Map<String, Object> status = new HashMap<>();
        try {
            redisService.set("health:check", "ok", 10, java.util.concurrent.TimeUnit.SECONDS);
            String val = redisService.get("health:check", String.class);
            status.put("connected", "ok".equals(val));
            status.put("message", "Redis 连接正常");
            Set<String> keys = redisService.keys("products:*");
            status.put("cachedProductKeys", keys != null ? keys.size() : 0);
            Set<String> allKeys = redisService.keys("*");
            status.put("totalKeys", allKeys != null ? allKeys.size() : 0);
        } catch (Exception e) {
            status.put("connected", false);
            status.put("message", "Redis 未连接: " + e.getMessage());
            status.put("cachedProductKeys", 0);
            status.put("totalKeys", 0);
        }
        return ok(status);
    }
}
