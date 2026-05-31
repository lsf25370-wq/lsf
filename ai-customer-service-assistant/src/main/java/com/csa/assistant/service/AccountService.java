package com.csa.assistant.service;

import com.csa.assistant.entity.*;
import com.csa.assistant.mapper.*;
import com.csa.assistant.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountMapper accountMapper;
    private final CouponMapper couponMapper;
    private final UserService userService;

    public AccountService(AccountMapper accountMapper, CouponMapper couponMapper, UserService userService) {
        this.accountMapper = accountMapper;
        this.couponMapper = couponMapper;
        this.userService = userService;
    }

    public AccountInfo getAccountInfo(String userId) {
        UserEntity user = userService.getUserById(userId);
        if (user == null) return null;

        AccountInfo info = new AccountInfo();
        info.setUserId(user.getUserId());
        info.setUserName(user.getUsername());
        info.setEmail(user.getEmail());
        info.setPhone(user.getPhone());
        info.setBalance(user.getBalance());
        info.setPoints(user.getPoints());
        info.setMemberLevel(user.getMemberLevel());
        info.setMemberExpireDate(user.getMemberExpireDate());
        info.setCoupons(getAvailableCoupons(userId));
        return info;
    }

    public List<Coupon> getAvailableCoupons(String userId) {
        return couponMapper.selectAvailableByUserId(userId).stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    public List<Coupon> getAllCoupons(String userId) {
        return couponMapper.selectByUserId(userId).stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    public Double getBalance(String userId) {
        UserEntity user = userService.getUserById(userId);
        return user != null ? user.getBalance() : 0.0;
    }

    public Integer getPoints(String userId) {
        UserEntity user = userService.getUserById(userId);
        return user != null ? user.getPoints() : 0;
    }

    public void updateBalance(String userId, Double newBalance) {
        UserEntity user = userService.getUserById(userId);
        if (user != null) {
            Double diff = newBalance - user.getBalance();
            if (diff > 0) {
                userService.increaseBalance(userId, diff);
            } else if (diff < 0) {
                userService.decreaseBalance(userId, -diff);
            }
        }
    }

    private Coupon convertToModel(CouponEntity entity) {
        Coupon coupon = new Coupon();
        coupon.setCouponId(entity.getCouponId());
        coupon.setName(entity.getName());
        coupon.setDiscount(entity.getDiscount());
        coupon.setMinAmount(entity.getMinAmount());
        coupon.setExpireDate(entity.getExpireDate());
        coupon.setAvailable(entity.getAvailable() != null && entity.getAvailable());
        coupon.setStatus(entity.getAvailable() != null && entity.getAvailable() ? "可用" : "已过期");
        return coupon;
    }

    public Coupon claimCoupon(String couponId, String userId) {
        CouponEntity existing = couponMapper.selectByCouponId(couponId);
        if (existing != null && existing.getUserId() != null) {
            return null; 
        }
        CouponEntity template = couponMapper.selectByCouponId(couponId);
        if (template == null) {
            return null;
        }
        List<CouponEntity> userCoupons = couponMapper.selectByUserId(userId);
        for (CouponEntity c : userCoupons) {
            if (c.getCouponId().equals(couponId)) {
                return null;
            }
        }
        CouponEntity newCoupon = new CouponEntity();
        String newCouponId = couponId + "_" + userId + "_" + System.currentTimeMillis();
        newCoupon.setCouponId(newCouponId);
        newCoupon.setName(template.getName());
        newCoupon.setDiscount(template.getDiscount());
        newCoupon.setMinAmount(template.getMinAmount());
        newCoupon.setExpireDate(template.getExpireDate());
        newCoupon.setAvailable(true);
        newCoupon.setUserId(userId);
        int result = couponMapper.insert(newCoupon);
        if (result > 0) {
            return convertToModel(newCoupon);
        }
        return null;
    }

    public List<Coupon> getAllTemplateCoupons() {
        return couponMapper.selectAllTemplates().stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }
}
