package com.csa.assistant.service;

import com.csa.assistant.entity.PointsExchangeEntity;
import com.csa.assistant.entity.PointsProductEntity;
import com.csa.assistant.mapper.PointsProductMapper;
import com.csa.assistant.model.PointsExchange;
import com.csa.assistant.model.PointsProduct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PointsMallService {

    private final PointsProductMapper pointsProductMapper;
    private final UserService userService;

    public PointsMallService(PointsProductMapper pointsProductMapper, UserService userService) {
        this.pointsProductMapper = pointsProductMapper;
        this.userService = userService;
    }

    public List<PointsProduct> getAllProducts(String category) {
        List<PointsProductEntity> entities;
        if (category != null && !category.isEmpty()) {
            entities = pointsProductMapper.selectByCategory(category);
        } else {
            entities = pointsProductMapper.selectAll();
        }
        return entities.stream().map(this::toProductModel).collect(Collectors.toList());
    }

    public PointsProduct getProductById(String productId) {
        PointsProductEntity entity = pointsProductMapper.selectByProductId(productId);
        return entity != null ? toProductModel(entity) : null;
    }

    @Transactional
    public PointsExchange exchangeProduct(String userId, String productId) {
        System.out.println("[PointsMall] exchangeProduct: userId=" + userId + ", productId=" + productId);
        
        PointsProductEntity product = pointsProductMapper.selectByProductId(productId);
        if (product == null) {
            System.err.println("[PointsMall] Product not found: " + productId);
            return null;
        }
        if (!"ON_SALE".equals(product.getStatus())) {
            System.err.println("[PointsMall] Product not on sale: " + product.getStatus());
            return null;
        }

        System.out.println("[PointsMall] Product found: " + product.getProductName() + ", stock=" + product.getStock() + ", pointsCost=" + product.getPointsCost());
        
        if (product.getStock() <= 0) {
            System.err.println("[PointsMall] Stock is 0");
            return null;
        }

        var user = userService.getUserById(userId);
        if (user == null) {
            System.err.println("[PointsMall] User not found: " + userId);
            return null;
        }
        System.out.println("[PointsMall] User points: " + user.getPoints() + ", need: " + product.getPointsCost());

        boolean success = userService.decreasePoints(userId, product.getPointsCost());
        if (!success) {
            System.err.println("[PointsMall] decreasePoints failed (insufficient points)");
            return null;
        }
        System.out.println("[PointsMall] Points deducted successfully");

        int decreased = pointsProductMapper.decreaseStock(productId);
        if (decreased <= 0) {
            System.err.println("[PointsMall] decreaseStock failed");
            userService.addPoints(userId, product.getPointsCost());
            return null;
        }
        System.out.println("[PointsMall] Stock decreased successfully");

        PointsExchangeEntity exchange = new PointsExchangeEntity();
        exchange.setExchangeId("EX" + System.currentTimeMillis());
        exchange.setUserId(userId);
        exchange.setProductId(productId);
        exchange.setProductName(product.getProductName());
        exchange.setPointsCost(product.getPointsCost());
        exchange.setStatus("待发货");
        exchange.setExchangeTime(java.time.LocalDateTime.now().toString());
        pointsProductMapper.insertExchange(exchange);
        System.out.println("[PointsMall] Exchange created: " + exchange.getExchangeId());

        return toExchangeModel(exchange);
    }

    public List<PointsExchange> getUserExchanges(String userId) {
        return pointsProductMapper.selectByUserId(userId).stream()
                .map(this::toExchangeModel)
                .collect(Collectors.toList());
    }

    public List<PointsExchange> getAllExchanges() {
        return pointsProductMapper.selectAllExchanges().stream()
                .map(this::toExchangeModel)
                .collect(Collectors.toList());
    }

    @Transactional
    public PointsProduct createProduct(String productName, String category, Integer pointsCost, Integer stock, String description) {
        PointsProductEntity entity = new PointsProductEntity();
        entity.setProductId("PP" + System.currentTimeMillis());
        entity.setProductName(productName);
        entity.setCategory(category);
        entity.setPointsCost(pointsCost);
        entity.setStock(stock);
        entity.setDescription(description);
        entity.setStatus("ON_SALE");
        entity.setCreateTime(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(java.time.LocalDateTime.now()));
        pointsProductMapper.insertProduct(entity);
        return toProductModel(entity);
    }

    @Transactional
    public PointsProduct updateProduct(String productId, String productName, Integer pointsCost, Integer stock, String status) {
        PointsProductEntity entity = pointsProductMapper.selectByProductId(productId);
        if (entity == null) return null;
        
        if (productName != null) entity.setProductName(productName);
        if (pointsCost != null) entity.setPointsCost(pointsCost);
        if (stock != null) entity.setStock(stock);
        if (status != null) entity.setStatus(status);
        
        pointsProductMapper.updateProduct(entity);
        return toProductModel(entity);
    }

    @Transactional
    public boolean deleteProduct(String productId) {
        return pointsProductMapper.deleteProduct(productId) > 0;
    }

    @Transactional
    public PointsExchange shipExchange(String exchangeId, String trackingNo) {
        PointsExchangeEntity entity = findExchangeById(exchangeId);

        if (entity == null || !"待发货".equals(entity.getStatus())) {
            return null;
        }

        pointsProductMapper.updateExchangeStatus(exchangeId, "已发货", trackingNo);
        entity = findExchangeById(exchangeId);
        return entity != null ? toExchangeModel(entity) : null;
    }

    private PointsExchangeEntity findExchangeById(String exchangeId) {
        return pointsProductMapper.selectAllExchanges().stream()
                .filter(e -> exchangeId.equals(e.getExchangeId()))
                .findFirst().orElse(null);
    }

    private PointsProduct toProductModel(PointsProductEntity entity) {
        PointsProduct model = new PointsProduct();
        model.setProductId(entity.getProductId());
        model.setProductName(entity.getProductName());
        model.setCategory(entity.getCategory());
        model.setPointsCost(entity.getPointsCost());
        model.setStock(entity.getStock());
        model.setDescription(entity.getDescription());
        model.setImageUrl(entity.getImageUrl());
        model.setStatus(entity.getStatus());
        return model;
    }

    private PointsExchange toExchangeModel(PointsExchangeEntity entity) {
        PointsExchange model = new PointsExchange();
        model.setExchangeId(entity.getExchangeId());
        model.setUserId(entity.getUserId());
        model.setProductId(entity.getProductId());
        model.setProductName(entity.getProductName());
        model.setPointsCost(entity.getPointsCost());
        model.setStatus(entity.getStatus());
        model.setExchangeTime(entity.getExchangeTime());
        model.setShipTime(entity.getShipTime());
        model.setTrackingNo(entity.getTrackingNo());
        return model;
    }
}
