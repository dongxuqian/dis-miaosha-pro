package com.dong.dis.api.cache.vo;

import java.io.Serializable;

/**
 * 访问次数的key前缀
 * @author dong
 */
public class AccessKeyPrefix extends BaseKeyPrefix implements Serializable {
    public  AccessKeyPrefix(int expireSeconds,String prefix){
        super(expireSeconds,prefix);
    }
    public AccessKeyPrefix(String prefix){
        super(prefix);
    }
    //灵活设置过期时间
    public  static  AccessKeyPrefix withExpire(int expireSeconds){
        return new AccessKeyPrefix(expireSeconds,"access");
    }
}
