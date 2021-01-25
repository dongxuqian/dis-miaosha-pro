package com.dong.dis.goods.persistence;

import com.dong.dis.api.goods.vo.GoodsVo;
import com.dong.dis.domain.Goods;
import com.dong.dis.domain.SeckillGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * goods表的数据库访问
 * @author dong
 */
@Mapper
public interface GoodsMapper {
    /**
     * 查出商品信息（包含该商品和秒杀信息）
     * 利用左外连接（left join on）
     */
    @Select("SELECT g.*, mg.stock_count, mg.start_date, mg.end_date, mg.seckill_price FROM seckill_goods mg LEFT JOIN goods g ON mg.goods_id=g.id")
    List<GoodsVo> listGoodsVo();

    /**
     * 通过商品 id 查出商品所有的信息
     */
    @Select("SELECT g.*, mg.stock_count, mg.start_date, mg.end_date, mg.seckill_price FROM seckill_goods mg LEFT JOIN goods g ON mg.goods_id=g.id where g.id = #{goodsId}")
    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") Long goodsId);

    /**
     * 减少seckill_goods库存
     * 增加库存判断 stock_count>0, 使得数据库不存在卖超问题!!!(数据库执行单条语句自带锁)
     */
    @Update("UPDATE seckill_goods SET stock_count = stock_count-1 WHERE goods_id=#{goodsId} AND stock_count > 0")
    int reduceStack(SeckillGoods seckillGoods);
}
