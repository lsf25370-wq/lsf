package com.csa.assistant.mapper;

import com.csa.assistant.entity.LogisticsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LogisticsMapper {

    LogisticsEntity selectByTrackingNo(@Param("trackingNo") String trackingNo);

    int insert(LogisticsEntity logistics);

    int update(LogisticsEntity logistics);

    int deleteByTrackingNo(@Param("trackingNo") String trackingNo);
}
