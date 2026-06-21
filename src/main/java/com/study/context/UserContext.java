package com.study.context;


import com.study.entity.User;

public class UserContext {

    private static final ThreadLocal<User> USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void setUser(User user) {
        USER_THREAD_LOCAL.set(user);
    }

    public static User getUser() {
        return USER_THREAD_LOCAL.get();
    }

    public static Long getUserId() {
        User user = getUser();
        return user != null ? user.getId() : null;
    }

    public static String getUsername() {
        User user = getUser();
        return user != null ? user.getUsername() : null;
    }

    public static String getNickname() {
        User user = getUser();
        return user != null ? user.getNickname() : null;
    }

    public static boolean isLogin() {
        return getUser() != null;
    }

    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }
}