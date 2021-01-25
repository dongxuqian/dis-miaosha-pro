package com.dong.dis.api.cache;

/**
 * 分布式锁接口
 * @author dong
 */
public interface DLockApi {
    /**
     * 获取锁
     * @param lockKey 锁
     * @param uniqueValue 能够唯一标识请求的值，以此保证锁的加解锁是同一个客户端
     * @param expireTime 过期时间
     * @return
     */
    boolean lock(String lockKey,String uniqueValue,int expireTime);

    /**
     * 释放锁
     * @param lockKey 锁
     * @param uniqueValue 能够唯一标识请求的值，以此保证锁的加解锁是同一个客户端
     * @return
     */
    boolean  unlock(String lockKey,String uniqueValue);
}
