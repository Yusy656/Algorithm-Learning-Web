package com.smartlearning.controller;

import com.smartlearning.dto.ApiResponse;
import com.smartlearning.dto.LoginRequest;
import com.smartlearning.dto.RegisterRequest;
import com.smartlearning.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     * @param request 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            Map<String, Object> result = userService.register(request);
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ApiResponse.success(result.get("message").toString(), (Map<String, Object>) result.get("data"));
            } else {
                return ApiResponse.fail(result.get("message").toString());
            }
        } catch (Exception e) {
            return ApiResponse.fail("注册失败：" + e.getMessage());
        }
    }

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            Map<String, Object> result = userService.login(request);
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ApiResponse.success(result.get("message").toString(), (Map<String, Object>) result.get("data"));
            } else {
                return ApiResponse.fail(result.get("message").toString());
            }
        } catch (Exception e) {
            return ApiResponse.fail("登录失败：" + e.getMessage());
        }
    }
}