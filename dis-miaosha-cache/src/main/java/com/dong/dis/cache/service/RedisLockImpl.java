package com.dong.dis.cache.service;

import com.dong.dis.api.cache.DLockApi;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;

/**
 * 分布式锁
 */
@Service(interfaceClass = DLockApi.class)
public class RedisLockImpl implements  DLockApi {
    @Autowired
    private JedisPool jedisPool;
    private final String LOCK_SUCCESS = "OK";
    private final String SET_IF_NOT_EXIST = "NX";
    private final String SET_WITH_EXPIRE_TIME = "PX";
    private final Long RELEASE_SUCCESS = 1L;

    @Override
    public boolean lock(String lockKey, String uniqueValue, int expireTime) {
        Jedis jedis  =null;
        try{
            jedis = jedisPool.getResource();
            String result = jedis.set(lockKey,uniqueValue,SET_IF_NOT_EXIST,SET_WITH_EXPIRE_TIME,expireTime);
            if(LOCK_SUCCESS.equals(result)){
                return true;
            }
            return false;
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }

    }

    /**
     * 使用Lua脚本保证解锁的原子性
     * @param lockKey 锁
     * @param uniqueValue 能够唯一标识请求的值，以此保证锁的加解锁是同一个客户端
     * @return
     */
    @Override
    public boolean unlock(String lockKey, String uniqueValue) {
        Jedis  jedis = null;
        try{
            jedis = jedisPool.getResource();
            //使用lua脚本保证操作的原子性
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('del', KEYS[1]) " +
                    "else return 0 " +
                    "end";
            Object result = jedis.eval(script, Collections.singletonList(lockKey),Collections.singletonList(uniqueValue));
            if(RELEASE_SUCCESS.equals(result)){
                return true;
            }
            return false;
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }
}
