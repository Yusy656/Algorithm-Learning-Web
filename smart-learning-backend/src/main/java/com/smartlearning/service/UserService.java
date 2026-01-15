package com.smartlearning.service;

import com.smartlearning.model.User;
import com.smartlearning.dto.LoginRequest;
import com.smartlearning.dto.RegisterRequest;
import com.smartlearning.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务类
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     * @param request 注册请求
     * @return 注册结果
     */
    public Map<String, Object> register(RegisterRequest request) {
        Map<String, Object> result = new HashMap<>();

        // 检查学号是否已存在
        User existingUser = userRepository.findByStudentId(request.getStudentId());
        if (existingUser != null) {
            result.put("success", false);
            result.put("message", "学号已存在，请使用其他学号注册");
            return result;
        }

        // 创建新用户
        User user = new User();
        user.setStudentId(request.getStudentId());
        user.setPassword(request.getPassword()); // 实际项目中应该对密码进行加密

        // 保存用户
        userRepository.save(user);

        result.put("success", true);
        result.put("message", "注册成功");
        result.put("data", Map.of("userId", user.getId()));

        return result;
    }

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录结果
     */
    public Map<String, Object> login(LoginRequest request) {
        Map<String, Object> result = new HashMap<>();

        // 根据学号查找用户
        User user = userRepository.findByStudentId(request.getStudentId());
        if (user == null) {
            result.put("success", false);
            result.put("message", "学号不存在");
            return result;
        }

        // 验证密码
        if (!user.getPassword().equals(request.getPassword())) { // 实际项目中应该使用加密密码进行验证
            result.put("success", false);
            result.put("message", "密码错误");
            return result;
        }

        // 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getStudentId());

        result.put("success", true);
        result.put("message", "登录成功");
        result.put("data", Map.of(
                "userId", user.getId(),
                "token", token
        ));

        return result;
    }

    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户
     */
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * 根据学号获取用户
     * @param studentId 学号
     * @return 用户
     */
    public User getUserByStudentId(String studentId) {
        return userRepository.findByStudentId(studentId);
    }
}