package com.dong.dis.api.order;

import com.dong.dis.api.goods.vo.GoodsVo;
import com.dong.dis.api.user.vo.UserVo;
import com.dong.dis.domain.OrderInfo;
import com.dong.dis.domain.SeckillOrder;

/**
 * 订单服务接口
 * @author dong
 */
public interface OrderServiceApi {
    /**
     * 通过订单id获取订单
     * @param orderId
     * @return
     */
    OrderInfo getOrderById(long orderId);

    /**
     * 通过用户和商品id获取订单信息，这用到了唯一索引（unique index）!
     * @param userId
     * @param goodsId
     * @return
     */
    SeckillOrder getOrderById(long userId,long goodsId);

    /**
     * 创建订单
     * @param user
     * @param goods
     * @return
     */
    OrderInfo createOrder(UserVo user, GoodsVo goods);
}
