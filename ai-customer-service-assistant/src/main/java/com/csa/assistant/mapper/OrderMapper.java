package com.csa.assistant.mapper;

import com.csa.assistant.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

    OrderEntity selectByOrderId(@Param("orderId") String orderId);

    List<OrderEntity> selectByStatus(@Param("status") String status);

    List<OrderEntity> selectByUserId(@Param("userId") String userId);

    List<OrderEntity> searchOrders(@Param("keyword") String keyword);

    List<OrderEntity> selectAll();

    int insert(OrderEntity order);

    int update(OrderEntity order);

    int deleteByOrderId(@Param("orderId") String orderId);
}
