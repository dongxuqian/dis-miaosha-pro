package com.dong.dis.api.cache;

import com.dong.dis.api.cache.vo.KeyPrefix;

/**
 * redis 服务接口
 * @author dong
 */
public interface RedisServiceApi {
    /**
     * redis 的get操作 ，获取redis对象
     * @param prefix  key的前缀
     * @param key   业务层的key
     * @param clazz  对象类型（redis以字符串存储）
     * @param <T>  指定类型
     * @return  返回对象
     */
    public <T> T get(KeyPrefix prefix,String key,Class<T> clazz);

    /**
     * redis 的set操作
     * @param prefix 键的前缀
     * @param key  键
     * @param value 值
     * @param <T>
     * @return  是否成功
     */
    public <T>  boolean set(KeyPrefix prefix,String key,T value);

    /**
     * 判断key是否存在redis中
     * @param Prefix  key前缀
     * @param key   key
     * @return
     */
    public  boolean exists(KeyPrefix Prefix,String key);

    /**
     * 自增，
     * @param prefix
     * @param key
     * @return
     */
    public  long incr(KeyPrefix prefix,String key);

    /**
     * 自减，用于秒杀商品数量
     * @param prefix
     * @param key
     * @return
     */
    public  long decr(KeyPrefix prefix,String key);

    /**
     * 删除缓存中的用户数据
     * @param prefix
     * @param key
     * @return
     */
    public  boolean delete(KeyPrefix prefix,String key);
}
