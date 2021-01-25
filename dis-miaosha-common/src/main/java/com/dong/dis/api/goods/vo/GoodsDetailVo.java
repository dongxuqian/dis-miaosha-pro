package com.dong.dis.api.goods.vo;

import com.dong.dis.api.user.vo.UserVo;

import java.io.Serializable;

/**
 * 商品详情，用于将数据传递给客户端
 * @author dong
 */
public class GoodsDetailVo implements Serializable {
    private int secKillStatus = 0;
    private int remainSeconds = 0;
    private GoodsVo goods;
    private UserVo user;

    @Override
    public String toString() {
        return "GoodsDetailVo{" +
                "secKillStatus=" + secKillStatus +
                ", remainSeconds=" + remainSeconds +
                ", goods=" + goods +
                ", user=" + user +
                '}';
    }

    public int getSecKillStatus() {
        return secKillStatus;
    }

    public void setSecKillStatus(int secKillStatus) {
        this.secKillStatus = secKillStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }

    public UserVo getUser() {
        return user;
    }

    public void setUser(UserVo user) {
        this.user = user;
    }
}
