package com.csa.assistant.mapper;

import com.csa.assistant.entity.AccountEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountMapper {

    AccountEntity selectByUserId(@Param("userId") String userId);

    int insert(AccountEntity account);

    int update(AccountEntity account);

    int updateBalance(@Param("userId") String userId, @Param("balance") Double balance);

    int updatePoints(@Param("userId") String userId, @Param("points") Integer points);
}
