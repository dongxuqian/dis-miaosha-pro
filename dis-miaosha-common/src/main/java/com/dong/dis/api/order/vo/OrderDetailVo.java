package com.dong.dis.api.order.vo;

import com.dong.dis.api.goods.vo.GoodsVo;
import com.dong.dis.api.user.vo.UserVo;
import com.dong.dis.domain.OrderInfo;

import java.io.Serializable;

/**
 * 订单详情
 * @author dong
 */
public class OrderDetailVo implements Serializable {
    private UserVo user;
    private GoodsVo goods;
    private OrderInfo order;

    public UserVo getUser() {
        return user;
    }

    public void setUser(UserVo user) {
        this.user = user;
    }

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }

    public OrderInfo getOrder() {
        return order;
    }

    public void setOrder(OrderInfo order) {
        this.order = order;
    }
}
