package com.dong.dis.api.cache.vo;

import java.io.Serializable;

/**
 * 存储订单的key前缀
 * @author dong
 */
public class OrderKeyPrefix extends BaseKeyPrefix implements Serializable {
    public OrderKeyPrefix(int expireSeconds,String prefix){
        super(expireSeconds,prefix);
    }
    public OrderKeyPrefix(String prefix){
        super(prefix);
    }
    public  static  OrderKeyPrefix getSeckillOrderByUidGid = new OrderKeyPrefix("getSeckillOrderByUidGid");
    public  static  OrderKeyPrefix SK_ORDER = new OrderKeyPrefix("SK_ORDER");
}
