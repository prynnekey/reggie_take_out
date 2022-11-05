package com.itheima.reggie.common;

/**
 * 基于ThreadLocal封装工具类，用于保存和获取当前登录的id
 * @author prynn
 */
public class BaseContext {
    private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        THREAD_LOCAL.set(userId);
    }

    public static Long getUserId() {
        return THREAD_LOCAL.get();
    }


    public static void remove(){
        THREAD_LOCAL.remove();
    }
}
