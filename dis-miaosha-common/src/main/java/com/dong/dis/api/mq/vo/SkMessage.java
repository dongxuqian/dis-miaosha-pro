package com.dong.dis.api.mq.vo;

import com.dong.dis.api.user.vo.UserVo;

import java.io.Serializable;

/**
 * 在mq中传递的秒杀信息
 * 包含参与秒杀的用户和商品id
 * @author dong
 */
public class SkMessage implements Serializable {
    private UserVo user;
    private long goodsId;

    public UserVo getUser() {
        return user;
    }

    public void setUser(UserVo user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "SkMessage{" +
                "user=" + user +
                ", goodsId=" + goodsId +
                '}';
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}
