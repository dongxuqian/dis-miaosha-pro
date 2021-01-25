package com.dong.dis.goods.service;

import com.dong.dis.api.Seckill.SeckillServiceApi;
import com.dong.dis.api.cache.RedisServiceApi;
import com.dong.dis.api.cache.vo.GoodsKeyPrefix;
import com.dong.dis.api.cache.vo.SkKeyPrefix;
import com.dong.dis.api.goods.GoodsServiceApi;
import com.dong.dis.api.goods.vo.GoodsVo;
import com.dong.dis.api.mq.MqProviderApi;
import com.dong.dis.api.mq.vo.SkMessage;
import com.dong.dis.api.order.OrderServiceApi;
import com.dong.dis.api.user.vo.UserVo;
import com.dong.dis.domain.OrderInfo;
import com.dong.dis.domain.SeckillOrder;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 秒杀服务实现类
 * @author dong
 */
@Service(interfaceClass = SeckillServiceApi.class)
public class SeckillServiceImpl implements SeckillServiceApi {
    @Autowired
    GoodsServiceApi goodsService;
    @Reference(interfaceClass = OrderServiceApi.class)
    OrderServiceApi orderService;
    @Reference(interfaceClass = RedisServiceApi.class)
    RedisServiceApi redisService;
    @Reference(interfaceClass = MqProviderApi.class)
    MqProviderApi  mqProviderService;
    @Override
    public String createVerifyCode(UserVo user, long goodsId) {
        return "";
    }
   // @Transactional
    @Override
    public OrderInfo seckill(UserVo user, GoodsVo goods) {
        SkMessage message = new SkMessage();
        message.setGoodsId(user.getPhone());
        message.setGoodsId(goods.getId());
        try {//1.减库存

            boolean success = goodsService.reduceStock(goods);
            if (!success) {
                setGoodsOver(goods.getId());
                return null;
            }
            //2.生成订单
            OrderInfo order = orderService.createOrder(user, goods);
            //3.更新缓存
            GoodsVo good = goodsService.getGoodsVoByGoodsId(goods.getId());
            redisService.set(GoodsKeyPrefix.GOODS_STOCK, "" + good.getId(), good.getStockCount());
            return order;
        }catch (Exception e ){
            mqProviderService.sendCancelOrder(message);
            return new OrderInfo();
        }
    }

    @Override
    public long getSeckillResult(Long userId, long goodsId) {
        SeckillOrder order = orderService.getOrderById(userId,goodsId);
        if(order!=null){
            return order.getOrderId();
        }else{
            boolean isOver = getGoodsOver(goodsId);
            if(isOver){
                return -1;
            }else{
                return 0;
            }
        }
    }
    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SkKeyPrefix.GOODS_SK_OVER, "" + goodsId);
    }
    private void setGoodsOver(long goodsId) {
        redisService.set(SkKeyPrefix.GOODS_SK_OVER, "" + goodsId, true);
    }

}
