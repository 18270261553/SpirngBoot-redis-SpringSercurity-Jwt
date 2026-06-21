package com.study.filter;
import com.study.context.UserContext;
import com.study.entity.User;
import com.study.service.UserCacheService;
import com.study.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserCacheService userCacheService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. 从请求头获取 Token
            String token = extractToken(request);

            if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
                // 2. 从 Token 中获取 userId
                Long userId = jwtUtil.getUserIdFromToken(token);

                // 3. 从 Redis 获取用户信息
                User user = userCacheService.getCachedUser(userId);

                if (user != null) {
                    // 4. 注入到 ThreadLocal
                    UserContext.setUser(user);

                    // 5. 设置 Spring Security 上下文
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            null,
                            Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_" + user.getRole())
                            )
                        );
                    authentication.setDetails(user);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("用户认证成功，userId: {}, username: {}", userId, user.getUsername());
                }
            }
        } catch (Exception e) {
            log.warn("JWT 认证失败: {}", e.getMessage());
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 请求完成后清理 ThreadLocal
            UserContext.clear();
        }
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}