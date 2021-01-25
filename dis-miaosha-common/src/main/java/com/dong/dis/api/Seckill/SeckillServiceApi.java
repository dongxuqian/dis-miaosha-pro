package com.dong.dis.api.Seckill;

import com.dong.dis.api.goods.vo.GoodsVo;
import com.dong.dis.api.user.vo.UserVo;
import com.dong.dis.domain.OrderInfo;
import org.springframework.util.StringUtils;

/**
 * 秒杀服务接口
 * @author dong
 */
public interface SeckillServiceApi {
    /**
     * 创建验证码
     * @param user
     * @param goodsId
     * @return
     */
    String createVerifyCode(UserVo user,long goodsId);

    /**
     * 执行秒杀操作：
     * 1.减库存
     * 2.生成的订单写入miaosha_order表中
     * @param user
     * @param goods
     * @return
     */
    OrderInfo seckill(UserVo user, GoodsVo goods);

    /**
     * 获取秒杀结果
     * @param userId
     * @param goodsId
     * @return
     */
    long getSeckillResult(Long userId,long goodsId);
}
