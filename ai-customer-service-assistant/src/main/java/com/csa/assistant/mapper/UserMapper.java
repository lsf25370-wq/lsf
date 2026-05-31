package com.csa.assistant.mapper;

import com.csa.assistant.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    UserEntity selectByUserId(@Param("userId") String userId);
    UserEntity selectByUsername(@Param("username") String username);
    List<UserEntity> selectAll();
    int insert(UserEntity user);
    int updateBalance(@Param("userId") String userId, @Param("balance") Double balance);
    int updatePoints(@Param("userId") String userId, @Param("points") Integer points);
    int updatePassword(@Param("userId") String userId, @Param("password") String password);
    int increaseBalance(@Param("userId") String userId, @Param("amount") Double amount);
    int decreaseBalance(@Param("userId") String userId, @Param("amount") Double amount);
    int increasePoints(@Param("userId") String userId, @Param("points") Integer points);
    int decreasePoints(@Param("userId") String userId, @Param("points") Integer points);
    int updateUserInfo(@Param("userId") String userId, @Param("email") String email, @Param("phone") String phone);
    int deleteUser(@Param("userId") String userId);
}
