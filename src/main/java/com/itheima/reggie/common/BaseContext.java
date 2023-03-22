package com.itheima.reggie.common;

/**
 * 基于ThreadLocal封装的工具类用于保存和获取当期用户的登录id
 */
public class BaseContext {
    private static  ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static void setCurrentId(Long id){
        threadLocal.set(id);

    }
    public static Long getCurrentId(){
        return threadLocal.get();
    }

}
