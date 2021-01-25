package com.dong.dis.cache.service;

import com.alibaba.fastjson.JSON;
import com.dong.dis.api.cache.RedisServiceApi;
import com.dong.dis.api.cache.vo.KeyPrefix;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service(interfaceClass = RedisServiceApi.class)
public class RedisServiceImpl implements RedisServiceApi {
    @Autowired
    JedisPool jedisPool;

    @Override
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        Jedis jedis  =null ;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            //redis里存的是 json格式  所以用字符串。
            String strValue = jedis.get(realKey);
            T objValue = stringToBean(strValue,clazz);
            return objValue;
        }finally {
            returnToPool(jedis);
        }
    }

    @Override
    public <T> boolean set(KeyPrefix prefix, String key, T value) {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String strValue = beanToString(value);
            if(strValue==null||strValue.length()<=0){
                return false;
            }
            String realKey = prefix.getPrefix()+key;
            int expires = prefix.expireSeconds();
            if(expires<=0){
                jedis.set(realKey,strValue);
            }else{
                jedis.setex(realKey,expires,strValue);
            }
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    @Override
    public boolean exists(KeyPrefix Prefix, String key) {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = Prefix.getPrefix()+key;
            return jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    @Override
    public long incr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    @Override
    public long decr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.decr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    @Override
    public boolean delete(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            Long del = jedis.del(realKey);
            return del > 0;
        } finally {
            returnToPool(jedis);
        }
    }
    public static <T> T  stringToBean(String strValue,Class<T> clazz){
        if ((strValue==null)||(strValue.length()<=0)||clazz==null) {
            return null;
        }
        if((clazz==int.class)||(clazz==Integer.class)){
            return  (T) Integer.valueOf(strValue);
        }
        else if((clazz==long.class)||(clazz==Long.class)){
            return (T) Long.valueOf(strValue);
        }else if(clazz==String.class){
            return (T)strValue;
        }else{
            return JSON.toJavaObject(JSON.parseObject(strValue),clazz);
        }
    }
    public  static <T>String beanToString(T value){
        if(value==null){
            return null;
        }
        Class<?> clazz = value.getClass();
        if(clazz == int.class||clazz==Integer.class){
            return value+"";
        }else if(clazz ==long.class||clazz==Long.class){
            return ""+value;
        }else if(clazz==String.class){
            return (String) value;
        }else {
            return JSON.toJSONString(value);
        }
    }
    public static void returnToPool(Jedis jedis){
        if(jedis!=null){
            jedis.close();
        }
    }
}
