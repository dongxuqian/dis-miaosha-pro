package com.dong.dis.mq.receiver;

import com.dong.dis.api.Seckill.SeckillServiceApi;
import com.dong.dis.api.cache.RedisServiceApi;
import com.dong.dis.api.cache.vo.OrderKeyPrefix;
import com.dong.dis.api.goods.GoodsServiceApi;
import com.dong.dis.api.goods.vo.GoodsVo;
import com.dong.dis.api.mq.vo.SkMessage;
import com.dong.dis.api.order.OrderServiceApi;
import com.dong.dis.api.user.vo.UserVo;

import com.dong.dis.domain.SeckillOrder;
import com.dong.dis.mq.config.MQConfig;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;


/**
 * 消费消息，监听消息队列
 * @author dong
 */

@Service
public class Mqconsumer {
    private static Logger logger = LoggerFactory.getLogger(Mqconsumer.class);
    @Reference(interfaceClass = GoodsServiceApi.class)
    GoodsServiceApi goodsService;
    @Reference(interfaceClass = OrderServiceApi.class)
    OrderServiceApi orderService;
    @Reference(interfaceClass = SeckillServiceApi.class)
    SeckillServiceApi seckillService;
    @Reference(interfaceClass = RedisServiceApi.class)
    RedisServiceApi redisService;


    /**
     * 核心业务实现
     * @param message
     */
    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
    public void receiveSkInfo(SkMessage message, Channel channel, Message msg){
        try {
            logger.info("MQ receive a message : " + message);
            UserVo user = message.getUser();
            long goodsId = message.getGoodsId();
            //获取库存
            GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
            Integer stockCount = goods.getStockCount();
            if (stockCount <= 0) {
                return;
            }
            //判断是否已经秒杀到
            SeckillOrder order = this.getSkOrderByUserIdAndGoodsId(user.getUuid(), goodsId);
            if (order != null) {
                return;
            }
            //1.减库存 2.写入订单，3 写入秒杀订单
            seckillService.seckill(user, goods);
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(),false);
        }catch(Exception e){

            if(msg.getMessageProperties().getRedelivered()){
                logger.info("消息已重复处理失败,拒绝再次接收...");
                try {
                    channel.basicReject(msg.getMessageProperties().getDeliveryTag(),false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }else{
                logger.info("消息即将再次返回队列处理...");
                try {
                    channel.basicNack(msg.getMessageProperties().getDeliveryTag(),false,true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    private SeckillOrder getSkOrderByUserIdAndGoodsId(Long userId,long goodsId){
        //从redis中取缓存，减少数据库访问时间
        SeckillOrder seckillOrder = redisService.get(OrderKeyPrefix.SK_ORDER,":"+"_"+goodsId,SeckillOrder.class);
        if(seckillOrder!=null){
            return seckillOrder;
        }
        return orderService.getOrderById(userId,goodsId);
    }
    @RabbitListener(queues=MQConfig.DELAY_QUEUE)
    public void delayHandler(SkMessage message, Channel channel, Message msg){
        System.out.println("数据库判断支付状态");
    }
    @RabbitListener(queues = MQConfig.ORDER_CANCEL_QUEUE)
    private void orderCancelHandler(SkMessage message, Channel channel, Message msg){
        System.out.println("如果tag 为删除订单，则删除订单");
    }
    @RabbitListener(queues = MQConfig.ORDER_CANCEL_QUEUE)
    private void stockCancelHandler(SkMessage message, Channel channel, Message msg){
        System.out.println("如果tag 为 加库存则数据库库存加1");
    }
}
