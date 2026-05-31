package com.csa.assistant.mapper;

import com.csa.assistant.entity.PaymentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaymentMapper {

    PaymentEntity selectByPaymentId(@Param("paymentId") String paymentId);

    PaymentEntity selectByOrderId(@Param("orderId") String orderId);

    List<PaymentEntity> selectByUserId(@Param("userId") String userId);
    List<PaymentEntity> selectAll();
    int insert(PaymentEntity payment);

    int update(PaymentEntity payment);

    int updateStatus(@Param("paymentId") String paymentId, @Param("status") String status);
}
