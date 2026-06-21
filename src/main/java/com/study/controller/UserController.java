package com.study.controller;

import com.study.context.UserContext;
import com.study.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
public class UserController {

    @GetMapping("/user/info")
    public User getUserInfo() {
        User user = UserContext.getUser();
        log.info(" UserContext.getUser() 返回值: {}", user);
        if (user != null) {
            log.info(" 用户信息: id={}, username={}, nickname={}",
                    user.getId(), user.getUsername(), user.getNickname());
        } else {
            log.warn(" UserContext 中没有用户信息");
        }
        return user;
    }

    @GetMapping("/user/me")
    public String getMe() {
        return "当前用户: " + UserContext.getNickname() + 
               " (ID: " + UserContext.getUserId() + ")";
    }
    //本项目暂时用不到，可忽略
    @GetMapping("/admin/check")
    public String adminOnly() {
        return "管理员权限验证通过，当前管理员: " + UserContext.getUsername();
    }
}