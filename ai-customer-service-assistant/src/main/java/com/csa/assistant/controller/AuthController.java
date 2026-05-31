package com.csa.assistant.controller;

import com.csa.assistant.entity.UserEntity;
import com.csa.assistant.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password) {
        UserEntity user = userService.login(username, password);
        Map<String, Object> result = new HashMap<>();
        if (user != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getUserId());
            data.put("username", user.getUsername());
            data.put("role", user.getRole());
            data.put("balance", user.getBalance());
            data.put("points", user.getPoints());
            data.put("memberLevel", user.getMemberLevel());
            data.put("email", user.getEmail());
            data.put("phone", user.getPhone());
            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("data", data);
        } else {
            result.put("code", 400);
            result.put("message", "用户名或密码错误");
            result.put("data", null);
        }
        return result;
    }

    @PostMapping("/register")
    public Map<String, Object> register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false, defaultValue = "") String email,
            @RequestParam(required = false, defaultValue = "") String phone) {
        Map<String, Object> result = new HashMap<>();
        int rows = userService.register(username, password, email, phone);
        if (rows > 0) {
            result.put("code", 200);
            result.put("message", "注册成功");
            result.put("data", null);
        } else if (rows == -1) {
            result.put("code", 400);
            result.put("message", "用户名已存在");
            result.put("data", null);
        } else {
            result.put("code", 500);
            result.put("message", "注册失败，请稍后重试");
            result.put("data", null);
        }
        return result;
    }
}
