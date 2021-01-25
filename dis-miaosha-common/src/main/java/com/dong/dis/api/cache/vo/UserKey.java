package com.dong.dis.api.cache.vo;

import java.io.Serializable;

/**
 * redis 中用来管理用户的key前缀
 * @author dong
 */
public class UserKey extends  BaseKeyPrefix implements Serializable {
    public  UserKey(String prefix){
        super(prefix);
    }
    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("name");
}
