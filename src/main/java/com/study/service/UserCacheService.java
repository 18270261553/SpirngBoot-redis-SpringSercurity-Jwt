package com.study.service;

import com.study.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String USER_CACHE_PREFIX = "user:info:";
    private static final long USER_CACHE_TTL = 3600; // 1小时（秒）

    /**
     * 缓存用户信息
     */
    public void cacheUser(User user) {
        if (user == null || user.getId() == null) {
            return;
        }
        String key = USER_CACHE_PREFIX + user.getId();
        try {
            redisTemplate.opsForValue().set(key, user, USER_CACHE_TTL, TimeUnit.SECONDS);
            log.info("用户信息已缓存，userId: {}, username: {}", user.getId(), user.getUsername());
        } catch (Exception e) {
            log.error("缓存用户信息失败，userId: {}", user.getId(), e);
        }
    }

    /**
     * 获取缓存的用户信息
     */
    public User getCachedUser(Long userId) {
        if (userId == null) {
            return null;
        }
        String key = USER_CACHE_PREFIX + userId;
        try {
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj != null) {
                return (User) obj;
            }
        } catch (Exception e) {
            log.error("获取缓存的用户信息失败，userId: {}", userId, e);
        }
        return null;
    }

    /**
     * 删除缓存的用户信息
     */
    public void evictUser(Long userId) {
        if (userId == null) {
            return;
        }
        String key = USER_CACHE_PREFIX + userId;
        try {
            Boolean deleted = redisTemplate.delete(key);
            if (Boolean.TRUE.equals(deleted)) {
                log.info("用户缓存已清除，userId: {}", userId);
            }
        } catch (Exception e) {
            log.error("清除用户缓存失败，userId: {}", userId, e);
        }
    }

    /**
     * 更新缓存（用户信息变更时调用）
     */
    public void updateCachedUser(User user) {
        if (user == null || user.getId() == null) {
            return;
        }
        evictUser(user.getId());
        cacheUser(user);
    }
}