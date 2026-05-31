package com.csa.assistant.service;

import com.csa.assistant.entity.PaymentEntity;
import com.csa.assistant.mapper.PaymentMapper;
import com.csa.assistant.model.Payment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final UserService userService;
    private final OrderService orderService;

    public PaymentService(PaymentMapper paymentMapper, UserService userService, OrderService orderService) {
        this.paymentMapper = paymentMapper;
        this.userService = userService;
        this.orderService = orderService;
    }

    public Payment getPaymentByOrderId(String orderId) {
        PaymentEntity entity = paymentMapper.selectByOrderId(orderId);
        return entity != null ? toModel(entity) : null;
    }

    public Payment getPaymentById(String paymentId) {
        PaymentEntity entity = paymentMapper.selectByPaymentId(paymentId);
        return entity != null ? toModel(entity) : null;
    }

    public List<Payment> getUserPayments(String userId) {
        return paymentMapper.selectByUserId(userId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<Payment> getAllPayments() {
        return paymentMapper.selectAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Transactional
    public Payment createPayment(String orderId, Double amount, String method, String userId) {
        System.out.println("[PaymentService] createPayment: orderId=" + orderId + ", amount=" + amount + ", method=" + method + ", userId=" + userId);
        
        PaymentEntity entity = new PaymentEntity();
        entity.setPaymentId("PAY" + System.currentTimeMillis());
        entity.setOrderId(orderId);
        entity.setAmount(amount);
        entity.setMethod(method);
        entity.setStatus("PENDING");
        entity.setUserId(userId);
        entity.setCreateTime(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(java.time.LocalDateTime.now()));
        paymentMapper.insert(entity);
        System.out.println("[PaymentService] Payment created: " + entity.getPaymentId());
        return toModel(entity);
    }

    @Transactional
    public synchronized Payment processPayment(String paymentId) {
        PaymentEntity entity = paymentMapper.selectByPaymentId(paymentId);
        if (entity == null) {
            System.err.println("[PaymentService] Payment not found: " + paymentId);
            return null;
        }
        if (!"PENDING".equals(entity.getStatus())) {
            System.err.println("[PaymentService] Payment status is not PENDING: " + entity.getStatus());
            return null;
        }

        String userId = entity.getUserId();
        Double amount = entity.getAmount();
        String method = entity.getMethod();

        System.out.println("[PaymentService] Processing payment: " + paymentId + ", userId=" + userId + ", amount=" + amount + ", method=" + method);

        if ("BALANCE".equals(method)) {
            var user = userService.getUserById(userId);
            if (user == null) {
                System.err.println("[PaymentService] User not found: " + userId);
                return null;
            }
            System.out.println("[PaymentService] User balance: " + user.getBalance() + ", need: " + amount);
            
            boolean success = userService.decreaseBalance(userId, amount);
            if (!success) {
                System.err.println("[PaymentService] Balance deduction failed for user: " + userId);
                entity.setStatus("FAILED");
                paymentMapper.update(entity);
                return null;
            }
            System.out.println("[PaymentService] Balance deducted successfully");
        }

        int earnedPoints = (int)(amount / 3);
        if (earnedPoints > 0) {
            userService.addPoints(userId, earnedPoints);
            System.out.println("[PaymentService] Added " + earnedPoints + " points to user: " + userId);
        }

        entity.setStatus("SUCCESS");
        entity.setPaidTime(java.time.LocalDateTime.now().toString());
        paymentMapper.update(entity);

        orderService.updateOrderStatus(entity.getOrderId(), "已支付");
        System.out.println("[PaymentService] Payment successful, order status updated");

        return toModel(entity);
    }

    @Transactional
    public synchronized Payment refundPayment(String paymentId) {
        PaymentEntity entity = paymentMapper.selectByPaymentId(paymentId);
        if (entity == null || !"SUCCESS".equals(entity.getStatus())) return null;

        String userId = entity.getUserId();
        Double amount = entity.getAmount();
        int refundPoints = (int)(amount / 3);

        if (refundPoints > 0) {
            userService.decreasePoints(userId, refundPoints);
        }

        userService.increaseBalance(userId, amount);

        entity.setStatus("REFUNDED");
        paymentMapper.update(entity);
        orderService.updateOrderStatus(entity.getOrderId(), "已退款");

        return toModel(entity);
    }

    @Transactional
    public synchronized Payment refundByOrderId(String orderId) {
        PaymentEntity entity = paymentMapper.selectByOrderId(orderId);
        if (entity == null || !"SUCCESS".equals(entity.getStatus())) return null;

        String userId = entity.getUserId();
        Double amount = entity.getAmount();
        int refundPoints = (int)(amount / 3);

        if (refundPoints > 0) {
            userService.decreasePoints(userId, refundPoints);
        }

        userService.increaseBalance(userId, amount);

        entity.setStatus("REFUNDED");
        paymentMapper.update(entity);
        orderService.updateOrderStatus(entity.getOrderId(), "已退款");

        return toModel(entity);
    }

    public String getPaymentSummary(String userId) {
        List<PaymentEntity> payments = paymentMapper.selectByUserId(userId);
        double totalPaid = payments.stream()
                .filter(p -> "SUCCESS".equals(p.getStatus()))
                .mapToDouble(PaymentEntity::getAmount).sum();
        long successCount = payments.stream()
                .filter(p -> "SUCCESS".equals(p.getStatus())).count();
        return "## 支付统计\n- 累计支付：¥" + String.format("%.2f", totalPaid)
                + "\n- 成功笔数：" + successCount + "\n- 总笔数：" + payments.size();
    }

    private Payment toModel(PaymentEntity entity) {
        Payment payment = new Payment();
        payment.setId(entity.getId());
        payment.setPaymentId(entity.getPaymentId());
        payment.setOrderId(entity.getOrderId());
        payment.setAmount(entity.getAmount());
        payment.setMethod(entity.getMethod());
        payment.setStatus(entity.getStatus());
        payment.setCreateTime(entity.getCreateTime());
        payment.setPaidTime(entity.getPaidTime());
        payment.setUserId(entity.getUserId());
        return payment;
    }
}
