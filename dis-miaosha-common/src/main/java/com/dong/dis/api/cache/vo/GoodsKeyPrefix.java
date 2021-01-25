package com.dong.dis.api.cache.vo;

import java.io.Serializable;

/**
 * redis中，用于商品信息的前缀
 * @author dong
 */
public class GoodsKeyPrefix extends   BaseKeyPrefix implements Serializable {
    public GoodsKeyPrefix(int expireSeconds,String prefix){
        super(expireSeconds, prefix);
    }
    /*
    缓存在redis中的商品列表页面的key的前缀
     */
    public  static GoodsKeyPrefix goodListKeyPrefix = new GoodsKeyPrefix(60,"goodsList");
    public  static  GoodsKeyPrefix GOODS_LIST_HTML = new GoodsKeyPrefix(60,"goodListHtml");
    /**
     * 缓存在redis中的商品详情页面的key的前缀
     */
    public static GoodsKeyPrefix goodsDetailKeyPrefix = new GoodsKeyPrefix(60,"goodsDetail");
    /**
     * 缓存在redis中的商品库存的前缀
     */
    public static  GoodsKeyPrefix seckillGoodsStockPrefix =  new GoodsKeyPrefix(0,"goodsStock");
    public  static  GoodsKeyPrefix GOODS_STOCK = new GoodsKeyPrefix(0,"goodsStock");
}
