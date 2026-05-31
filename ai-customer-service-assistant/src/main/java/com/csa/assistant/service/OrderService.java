package com.csa.assistant.service;

import com.csa.assistant.entity.OrderEntity;
import com.csa.assistant.mapper.OrderMapper;
import com.csa.assistant.model.Order;
import com.csa.assistant.model.Product;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderMapper orderMapper;

    public OrderService(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    public Order getOrderById(String orderId) {
        OrderEntity entity = orderMapper.selectByOrderId(orderId);
        return entity != null ? convertToModel(entity) : null;
    }

    public List<Order> getOrdersByStatus(String status) {
        return orderMapper.selectByStatus(status).stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    public List<Order> getOrdersByUserId(String userId) {
        return orderMapper.selectByUserId(userId).stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    public List<Order> getAllOrders() {
        return orderMapper.selectAll().stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    public List<Order> searchOrders(String keyword) {
        return orderMapper.searchOrders(keyword).stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    public Order createOrder(String productId, int quantity, String userId, ProductService productService) {
        Product product = productService.getProductById(productId);
        if (product == null || product.getStock() < quantity) {
            return null;
        }

        if (!productService.deductStock(productId, quantity)) {
            return null;
        }

        String orderId = "ORD" + System.currentTimeMillis();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        double totalAmount = product.getPrice() * quantity;

        OrderEntity entity = new OrderEntity();
        entity.setOrderId(orderId);
        entity.setCreateTime(createTime);
        entity.setStatus("待支付");
        entity.setCategory(product.getCategory());
        entity.setProductName(product.getName() + " ×" + quantity);
        entity.setAmount(totalAmount);
        entity.setReceiverName(extractReceiverName(userId));
        entity.setReceiverAddress("北京市朝阳区xxx街道xxx号");
        entity.setUserId(userId);
        orderMapper.insert(entity);

        return convertToModel(entity);
    }

    public Order cancelOrder(String orderId) {
        OrderEntity entity = orderMapper.selectByOrderId(orderId);
        if (entity == null) {
            return null;
        }
        if ("已签收".equals(entity.getStatus()) || "运输中".equals(entity.getStatus())) {
            return null;
        }
        entity.setStatus("已取消");
        orderMapper.update(entity);
        return convertToModel(entity);
    }

    public Order shipOrder(String orderId, String trackingNo, String logisticsCompany) {
        OrderEntity entity = orderMapper.selectByOrderId(orderId);
        if (entity == null || !"已支付".equals(entity.getStatus())) {
            return null;
        }
        entity.setStatus("运输中");
        entity.setTrackingNo(trackingNo);
        entity.setLogisticsCompany(logisticsCompany);
        orderMapper.update(entity);
        return convertToModel(entity);
    }

    public Order confirmDelivered(String orderId) {
        OrderEntity entity = orderMapper.selectByOrderId(orderId);
        if (entity == null || !"运输中".equals(entity.getStatus())) {
            return null;
        }
        entity.setStatus("已签收");
        orderMapper.update(entity);
        return convertToModel(entity);
    }

    public void updateOrderStatus(String orderId, String newStatus) {
        OrderEntity entity = orderMapper.selectByOrderId(orderId);
        if (entity != null) {
            entity.setStatus(newStatus);
            orderMapper.update(entity);
        }
    }

    private String extractReceiverName(String userId) {
        return "user001".equals(userId) ? "张三" : "用户" + userId;
    }

    private Order convertToModel(OrderEntity entity) {
        Order order = new Order();
        order.setOrderId(entity.getOrderId());
        order.setCreateTime(entity.getCreateTime());
        order.setStatus(entity.getStatus());
        order.setCategory(entity.getCategory());
        order.setProductName(entity.getProductName());
        order.setAmount(entity.getAmount());
        order.setReceiverName(entity.getReceiverName());
        order.setReceiverAddress(entity.getReceiverAddress());
        order.setTrackingNo(entity.getTrackingNo());
        order.setLogisticsCompany(entity.getLogisticsCompany());
        return order;
    }
}
