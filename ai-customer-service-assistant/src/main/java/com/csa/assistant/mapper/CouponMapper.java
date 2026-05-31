package com.csa.assistant.mapper;

import com.csa.assistant.entity.CouponEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CouponMapper {

    List<CouponEntity> selectByUserId(@Param("userId") String userId);

    List<CouponEntity> selectAvailableByUserId(@Param("userId") String userId);

    CouponEntity selectByCouponId(@Param("couponId") String couponId);

    int insert(CouponEntity coupon);

    int update(CouponEntity coupon);

    int deleteById(@Param("id") Long id);

    int updateStatus(@Param("couponId") String couponId, @Param("available") Boolean available);

    List<CouponEntity> selectAllTemplates();
}
