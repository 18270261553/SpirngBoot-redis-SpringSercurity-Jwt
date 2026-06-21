package com.study.service;
import com.study.dto.RegisterRequest;
import com.study.entity.User;
import com.study.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户注册
     */
    @Transactional
    public User register(RegisterRequest request) {
        // 1. 校验用户名是否已存在
        User existingUser = userMapper.findByUsername(request.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在: " + request.getUsername());
        }

        // 2. 校验密码长度（至少6位）
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new RuntimeException("密码长度不能少于6位");
        }

        // 3. 创建用户对象
        User user = new User();
        user.setUsername(request.getUsername());
        // 密码加密存储
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole("user");
        user.setStatus(1);

        // 4. 保存到数据库
        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new RuntimeException("注册失败，请稍后重试");
        }

        log.info("用户注册成功，username: {}, userId: {}", user.getUsername(), user.getId());
        return user;
    }
}