package com.csa.assistant.mapper;

import com.csa.assistant.entity.PointsProductEntity;
import com.csa.assistant.entity.PointsExchangeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PointsProductMapper {

    List<PointsProductEntity> selectAll();
    List<PointsProductEntity> selectByCategory(@Param("category") String category);
    PointsProductEntity selectByProductId(@Param("productId") String productId);
    int decreaseStock(@Param("productId") String productId);
    int insertProduct(PointsProductEntity product);
    int updateProduct(PointsProductEntity product);
    int deleteProduct(@Param("productId") String productId);

    List<PointsExchangeEntity> selectByUserId(@Param("userId") String userId);
    List<PointsExchangeEntity> selectAllExchanges();
    int insertExchange(PointsExchangeEntity exchange);
    int updateExchangeStatus(@Param("exchangeId") String exchangeId, @Param("status") String status, @Param("trackingNo") String trackingNo);
}
