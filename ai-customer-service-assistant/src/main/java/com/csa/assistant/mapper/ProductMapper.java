package com.csa.assistant.mapper;

import com.csa.assistant.entity.ProductEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    List<ProductEntity> selectAll();

    List<ProductEntity> selectAllWithStatus();

    ProductEntity selectByProductId(@Param("productId") String productId);

    List<ProductEntity> selectByCategory(@Param("category") String category);

    List<ProductEntity> searchProducts(@Param("keyword") String keyword);

    int insert(ProductEntity product);

    int update(ProductEntity product);

    int updateStock(@Param("productId") String productId, @Param("stock") Integer stock);

    int updateStatus(@Param("productId") String productId, @Param("status") String status);

    int deleteByProductId(@Param("productId") String productId);
}
