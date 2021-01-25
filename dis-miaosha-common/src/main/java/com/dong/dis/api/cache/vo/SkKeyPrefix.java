package com.dong.dis.api.cache.vo;

import java.io.Serializable;

/**
 * 判断秒杀状态的key的前缀
 * @author dong
 */
public class SkKeyPrefix extends BaseKeyPrefix implements Serializable {
    public SkKeyPrefix(String prefix){
        super(prefix);
    }
    public SkKeyPrefix(int expireSeconds,String prefix){
        super(expireSeconds,prefix);
    }
    public  static  SkKeyPrefix isGoodsOver = new SkKeyPrefix("isGoodsOver");
    //库存为0的商品的前缀，标志位，减少判断
    public static  SkKeyPrefix GOODS_SK_OVER  = new SkKeyPrefix("goodsSkOver");
    /**
     * 秒杀接口随机地址
     */
    public  static  SkKeyPrefix SkPath = new SkKeyPrefix(60,"skPath");
    public static SkKeyPrefix SK_PATH = new SkKeyPrefix(60,"skPath");
    //秒杀验证码，用于削峰限流,五分钟内有效
    public  static  SkKeyPrefix  skVerifyCode = new SkKeyPrefix(300,"skVerifyCode");
    public static  SkKeyPrefix VERIFY_RESULT = new SkKeyPrefix(300,"verifyResult");
}
