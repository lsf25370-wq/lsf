package com.csa.assistant.mapper;

import com.csa.assistant.entity.ReturnRequestEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReturnRequestMapper {

    ReturnRequestEntity selectByRequestId(@Param("requestId") String requestId);

    ReturnRequestEntity selectByOrderId(@Param("orderId") String orderId);

    List<ReturnRequestEntity> selectByUserId(@Param("userId") String userId);

    List<ReturnRequestEntity> selectAll();

    int insert(ReturnRequestEntity request);

    int update(ReturnRequestEntity request);

    int updateStatus(@Param("requestId") String requestId, @Param("status") String status);
}
