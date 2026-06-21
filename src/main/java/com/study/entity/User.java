package com.study.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String avatar;
    private String role;  // admin, user
    private Integer status;  // 0-禁用, 1-启用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}