package com.csa.assistant.service;

import com.csa.assistant.entity.ProductEntity;
import com.csa.assistant.mapper.ProductMapper;
import com.csa.assistant.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductMapper productMapper;

    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public List<Product> getAllProducts() {
        return productMapper.selectAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<Product> getAllProductsWithStatus() {
        return productMapper.selectAllWithStatus().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public Product getProductById(String productId) {
        ProductEntity entity = productMapper.selectByProductId(productId);
        return entity != null ? toModel(entity) : null;
    }

    public List<Product> getProductsByCategory(String category) {
        return productMapper.selectByCategory(category).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<Product> searchProducts(String keyword) {
        return productMapper.searchProducts(keyword).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public boolean deductStock(String productId, int quantity) {
        ProductEntity entity = productMapper.selectByProductId(productId);
        if (entity == null || entity.getStock() < quantity) {
            return false;
        }
        int newStock = entity.getStock() - quantity;
        productMapper.updateStock(productId, newStock);
        if (newStock == 0) {
            productMapper.updateStock(productId, 0);
        }
        return true;
    }

    public String getProductRecommendation(String query) {
        List<ProductEntity> products = productMapper.selectAll();
        if (products.isEmpty()) {
            return "暂无推荐商品";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("## 平台热销商品\n");
        for (int i = 0; i < Math.min(6, products.size()); i++) {
            ProductEntity p = products.get(i);
            sb.append("- **").append(p.getName()).append("** | ")
              .append("¥").append(p.getPrice())
              .append(" | 库存:").append(p.getStock())
              .append(" | 类目:").append(p.getCategory()).append("\n");
        }
        return sb.toString();
    }

    private Product toModel(ProductEntity entity) {
        Product product = new Product();
        product.setId(entity.getId());
        product.setProductId(entity.getProductId());
        product.setName(entity.getName());
        product.setCategory(entity.getCategory());
        product.setDescription(entity.getDescription());
        product.setPrice(entity.getPrice());
        product.setStock(entity.getStock());
        product.setImageUrl(entity.getImageUrl());
        product.setStatus(entity.getStatus());
        product.setCreateTime(entity.getCreateTime());
        return product;
    }

    @Transactional
    public Product createProduct(String productName, String category, Double price, Integer stock, String description) {
        ProductEntity entity = new ProductEntity();
        entity.setProductId("P" + System.currentTimeMillis());
        entity.setName(productName);
        entity.setCategory(category);
        entity.setPrice(price);
        entity.setStock(stock);
        entity.setDescription(description);
        entity.setStatus("ON_SALE");
        entity.setCreateTime(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(java.time.LocalDateTime.now()));
        productMapper.insert(entity);
        return toModel(entity);
    }

    @Transactional
    public void toggleProductStatus(String productId) {
        ProductEntity entity = productMapper.selectByProductId(productId);
        if (entity == null) return;
        String newStatus = "ON_SALE".equals(entity.getStatus()) ? "OFF_SALE" : "ON_SALE";
        productMapper.updateStatus(productId, newStatus);
    }

    @Transactional
    public Product updateProduct(String productId, String productName, String category, Double price, Integer stock, String description, String status) {
        ProductEntity entity = productMapper.selectByProductId(productId);
        if (entity == null) return null;
        if (productName != null) entity.setName(productName);
        if (category != null) entity.setCategory(category);
        if (price != null) entity.setPrice(price);
        if (stock != null) entity.setStock(stock);
        if (description != null) entity.setDescription(description);
        if (status != null) entity.setStatus(status);
        productMapper.update(entity);
        return toModel(entity);
    }

    @Transactional
    public boolean deleteProduct(String productId) {
        return productMapper.deleteByProductId(productId) > 0;
    }
}
