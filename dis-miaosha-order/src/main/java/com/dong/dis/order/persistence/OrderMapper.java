package com.dong.dis.order.persistence;

import com.dong.dis.domain.OrderInfo;
import com.dong.dis.domain.SeckillOrder;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderMapper {
    /**
     * 通过用户id与商品id从订单列表中获取订单信息
     * @param userId
     * @param goodsId
     * @return
     */
    @Select("select * from seckill_order where user_id = #{userId}and goods_id = #{goodsId}")
    SeckillOrder getSeckillOrderByUserIdAndGoodsId(@Param("userId") long userId,@Param("goodsId") long goodsId);

    /**
     * 将订单信息插入 order_info 表中
     * @param orderInfo
     * @return
     */
    @Insert("insert into order_info(user_if,goods_id,goods_count,goods_price,order_channel,status,creat_date) values(#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel}, #{status}, #{createDate})")
    @SelectKey(keyColumn = "id",keyProperty = "id",resultType = long.class,before = false, statement = "SELECT last_insert_id()")
    long insert(OrderInfo orderInfo);

    /**
     * 将秒杀订单信息插入到seckill_order中
     * @param seckillOrder
     */
    @Insert("insert into seckill_order (user_id, order_id, goods_id) VALUES (#{userId}, #{orderId}, #{goodsId})")
    void insertSeckillOrder(SeckillOrder seckillOrder);
    /**
     * 获取订单id
     * @param orderId
     * @return
     */
    @Select("select *from order_info where id =#{orderId}")
    OrderInfo getOrderById(@Param("orderId") long orderId);
}
