package com.dong.dis.goods.service;

import com.dong.dis.api.goods.GoodsServiceApi;
import com.dong.dis.api.goods.vo.GoodsVo;
import com.dong.dis.domain.SeckillGoods;
import com.dong.dis.goods.persistence.GoodsMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 商品服务接口实现
 * @author dong
 */
@Service(interfaceClass = GoodsServiceApi.class)
public class GoodsServiceImpl implements GoodsServiceApi {
    @Autowired
    GoodsMapper goodsMapper;

    @Override
    public List<GoodsVo> ListGoodsVo() {
        return goodsMapper.listGoodsVo();
    }

    @Override
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsMapper.getGoodsVoByGoodsId(goodsId);
    }

    @Override
    public GoodsVo getGoodsVoByGoodsId(Long goodsId) {
        return goodsMapper.getGoodsVoByGoodsId(goodsId);
    }

    @Override
    public boolean reduceStock(GoodsVo goodsVo) {
        SeckillGoods seckillGoods = new SeckillGoods();
        seckillGoods.setGoodsId(goodsVo.getId());
        int ret = goodsMapper.reduceStack(seckillGoods);
        return ret>0;
    }
}
