package com.csa.assistant.service;

import com.csa.assistant.entity.UserEntity;
import com.csa.assistant.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public UserEntity login(String username, String password) {
        UserEntity user = userMapper.selectByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public UserEntity getUserById(String userId) {
        return userMapper.selectByUserId(userId);
    }

    @Transactional
    public int register(String username, String password, String email, String phone) {
        return register(username, password, email, phone, "USER");
    }

    @Transactional
    public int register(String username, String password, String email, String phone, String role) {
        UserEntity existing = userMapper.selectByUsername(username);
        if (existing != null) return -1;
        UserEntity user = new UserEntity();
        user.setUserId("USER" + System.currentTimeMillis());
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        user.setEmail(email);
        user.setPhone(phone);
        user.setBalance("ADMIN".equals(role) ? 0.0 : 1000.0);
        user.setPoints(0);
        user.setMemberLevel("ADMIN".equals(role) ? null : "普通");
        user.setCreateTime(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(java.time.LocalDateTime.now()));
        return userMapper.insert(user);
    }

    @Transactional
    public boolean decreaseBalance(String userId, Double amount) {
        return userMapper.decreaseBalance(userId, amount) > 0;
    }

    @Transactional
    public void increaseBalance(String userId, Double amount) {
        userMapper.increaseBalance(userId, amount);
    }

    @Transactional
    public boolean decreasePoints(String userId, Integer points) {
        return userMapper.decreasePoints(userId, points) > 0;
    }

    @Transactional
    public boolean addPoints(String userId, Integer points) {
        return userMapper.increasePoints(userId, points) > 0;
    }

    @Transactional
    public void updateUserInfo(String userId, String email, String phone) {
        userMapper.updateUserInfo(userId, email, phone);
    }

    @Transactional
    public void updatePassword(String userId, String newPassword) {
        userMapper.updatePassword(userId, newPassword);
    }

    public java.util.List<UserEntity> getAllUsers() {
        return userMapper.selectAll();
    }

    @Transactional
    public void updateBalanceDirect(String userId, Double balance) {
        userMapper.updateBalance(userId, balance);
    }

    @Transactional
    public void updatePointsDirect(String userId, Integer points) {
        userMapper.updatePoints(userId, points);
    }

    @Transactional
    public boolean deleteUser(String userId) {
        return userMapper.deleteUser(userId) > 0;
    }
}
