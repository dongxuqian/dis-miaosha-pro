package com.dong.dis.api.goods;

import com.dong.dis.api.goods.vo.GoodsVo;

import java.util.List;

/**
 * 商品服务接口
 * @author dong
 */
public interface GoodsServiceApi  {
    /**
     * 获取商品列表
     * @return
     */
    List<GoodsVo> ListGoodsVo();

    /**
     * 通过id查商品（包含秒杀信息）
     * @param goodsId
     * @return
     */
    GoodsVo getGoodsVoByGoodsId(long goodsId);
    GoodsVo getGoodsVoByGoodsId(Long goodsId);

    /**
     * 减库存
     * @param goodsVo
     * @return
     */
    boolean reduceStock(GoodsVo goodsVo);
}
