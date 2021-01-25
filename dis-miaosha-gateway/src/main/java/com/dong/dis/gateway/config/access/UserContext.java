package com.dong.dis.gateway.config.access;

import com.dong.dis.api.user.vo.UserVo;

/**
 * 用于保存用户
 * 使用ThreadLocal保存用户，因为ThreadLocal是线程安全的，使用ThreadLocal可以保存当前线程持有的对象
 * 每个用户的请求对应一个线程，所以使用ThreadLocal以线程为键保存用户是合适的
 */
public class UserContext {
    private static ThreadLocal<UserVo> userHolder =  new ThreadLocal<>();
    public static void setUser(UserVo user){
        userHolder.set(user);
    }
    public static UserVo getUser(){
        return  userHolder.get();
    }
}
