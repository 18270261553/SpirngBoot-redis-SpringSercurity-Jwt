package com.study.controller;

import com.study.context.UserContext;
import com.study.dto.LoginRequest;
import com.study.dto.RegisterRequest;
import com.study.entity.User;
import com.study.mapper.UserMapper;
import com.study.service.UserCacheService;
import com.study.service.UserService;
import com.study.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserCacheService userCacheService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {
        // 1. 验证用户名密码
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 2. 获取用户信息
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userMapper.findByUsername(userDetails.getUsername());

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 3. 生成 JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 4. 缓存用户信息到 Redis
        userCacheService.cacheUser(user);

        // 5. 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("role", user.getRole());

        log.info("用户登录成功，userId: {}, username: {}", user.getId(), user.getUsername());
        return result;
    }
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody RegisterRequest request) {
        // 1. 注册用户
        User user = userService.register(request);
        log.info("用户注册成功，userId: {}, username: {}", user.getId(), user.getUsername());

        // 2. 自动生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        log.info("Token 生成成功: {}", token);

        // 3. 缓存用户信息到 Redis
        userCacheService.cacheUser(user);  // ← 确保这行没有被注释
        log.info("调用 cacheUser 完成，userId: {}", user.getId());

        // 4. 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "注册成功");
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("role", user.getRole());
        return result;
    }
    @PostMapping("/logout")
    public Map<String, String> logout() {
        Long userId = UserContext.getUserId();
        if (userId != null) {
            userCacheService.evictUser(userId);
            log.info("用户退出登录，userId: {}", userId);
        }
        Map<String, String> result = new HashMap<>();
        result.put("message", "退出成功");
        return result;
    }
}

