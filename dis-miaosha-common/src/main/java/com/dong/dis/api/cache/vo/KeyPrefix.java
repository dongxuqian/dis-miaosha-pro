package com.dong.dis.api.cache.vo;
/**
 * reids键的前缀
 * @author dong
 */
public interface KeyPrefix {
    /**
     * key的过期时间
     * @return 过期时间
     */
    int expireSeconds();

    /**
     * key的前缀
     * @return 前缀
     */
    String getPrefix();
}
