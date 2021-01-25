package com.dong.dis.gateway.seckill;

import com.dong.dis.api.Seckill.SeckillServiceApi;
import com.dong.dis.api.Seckill.vo.VerifyCodeVo;
import com.dong.dis.api.cache.RedisServiceApi;
import com.dong.dis.api.cache.vo.GoodsKeyPrefix;
import com.dong.dis.api.cache.vo.OrderKeyPrefix;
import com.dong.dis.api.cache.vo.SkKeyPrefix;
import com.dong.dis.api.goods.GoodsServiceApi;
import com.dong.dis.api.goods.vo.GoodsVo;
import com.dong.dis.api.mq.MqProviderApi;
import com.dong.dis.api.mq.vo.SkMessage;
import com.dong.dis.api.order.OrderServiceApi;
import com.dong.dis.api.user.vo.UserVo;
import com.dong.dis.domain.SeckillOrder;
import com.dong.dis.gateway.config.access.AccessLimit;
import com.dong.dis.result.CodeMsg;
import com.dong.dis.result.Result;
import com.dong.dis.util.MD5Util;
import com.dong.dis.util.UUIDUtil;
import com.dong.dis.util.VerifyCodeUtil;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 秒杀接口
 * @author dong
 */
@Controller
@RequestMapping("/seckill/")
public class SeckillController implements InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(SeckillController.class);
    @Reference(interfaceClass = RedisServiceApi.class)
    RedisServiceApi redisService;

    @Reference(interfaceClass = GoodsServiceApi.class)
    GoodsServiceApi goodsService;

    @Reference(interfaceClass = SeckillServiceApi.class)
    SeckillServiceApi seckillService;

    @Reference(interfaceClass = OrderServiceApi.class)
    OrderServiceApi orderService;

    @Reference(interfaceClass = MqProviderApi.class)
    MqProviderApi sender;
    /**
     * 用于内存标记，标记库存内存是否为空，从而减少redis访问
     */
    private Map<Long,Boolean> localOverMap = new HashMap<>();

    /**
     * 获取秒杀接口地址
     * 1. 每一次点击秒杀，都会生成一个随机的秒杀地址返回给客户端
     * 2. 对秒杀的次数做限制（通过自定义拦截器注解完成）
     *
     * @param model
     * @param user
     * @param goodsId    秒杀的商品id
     * @param verifyCode 验证码
     * @return 被隐藏的秒杀接口路径
     */
    @AccessLimit(seconds =  5,maxAccessCount = 5,needLogin = true)
    @RequestMapping(value = "path",method = RequestMethod.GET)
    public Result<String> getSeckillPath(Model model, UserVo user, @RequestParam("goodsId") long goodsId,
                                         @RequestParam(value = "verifyCode", defaultValue = "0" )int verifyCode){

        /** 在执行下面的逻辑之前，
         * 会先对path请求进行拦截处理（@AccessLimit， AccessInterceptor），
         * 防止访问次数过于频繁，对服务器造成过大的压力
         */

        model.addAttribute("user",user);
        if(goodsId<=0){
            return Result.error(CodeMsg.SECKILL_PARM_ILLEGAL.fillArgs("商品id小于0"));
        }
        //校验验证码
        boolean check = this.checkVerifyCode(user,goodsId,verifyCode);
        if(!check){
            return Result.error(CodeMsg.VERITF_FAIL);
        }
        String path = this.createSkPath(user,goodsId);
        return Result.success(path);
    }
    /**
     * 秒杀逻辑（页面静态化分离，不需要直接将页面返回给客户端，而是返回客户端需要的页面动态数据，返回数据时json格式）
     * <p>
     * QPS:1306
     * 5000 * 10
     * <p>
     * GET/POST的@RequestMapping是有区别的
     * <p>
     * 通过随机的path，客户端隐藏秒杀接口
     * <p>
     * 优化: 不同于每次都去数据库中读取秒杀订单信息，而是在第一次生成秒杀订单成功后，
     * 将订单存储在redis中，再次读取订单信息的时候就直接从redis中读取
     *
     * @param model
     * @param user
     * @param goodsId
     * @param path    隐藏的秒杀地址，为客户端回传的path，最初也是有服务端产生的
     * @return 订单详情或错误码
     */
    public Result<Integer> doSeckill(Model model, UserVo user, @RequestParam("goodsId") long goodsId, @PathVariable("path") String path
    ){
        model.addAttribute("user",user);
        //验证path
        boolean check = this.checkPath(user,goodsId,path);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //判断内存标记
        Boolean over = localOverMap.get(goodsId);
        if(over){
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //判断排队标记，redis 里加一个标记  判断  （prefix+goodid+uid）


        //

        //预减库存, 分布式锁  setnx或者 redission  或者 zookeeper
        Long stock = redisService.decr(GoodsKeyPrefix.GOODS_STOCK,""+goodsId);
        if(stock<0){
            localOverMap.put(goodsId,true);
            //redis 数量要加回来
            redisService.incr(GoodsKeyPrefix.GOODS_STOCK,""+goodsId);

            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //判断是否重复秒杀

        //redis中取缓存
        SeckillOrder order =redisService.get(OrderKeyPrefix.SK_ORDER,":"+user.getUuid()+"_"+goodsId,SeckillOrder.class);
        //如果缓存中不存在该数据，则从数据库取
        if(order==null){
            order = orderService.getOrderById(user.getUuid(),goodsId);
        }
        if(order!=null){
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }
        SkMessage message = new SkMessage();
        message.setUser(user);
        message.setGoodsId(goodsId);
        sender.sendSkMessage(message);
        sender.sendDelayMessage(message);
        return Result.success(0);
    }

    /**
     * 用于返回用户秒杀的结果
     *
     * @param model
     * @param user
     * @param goodsId
     * @return orderId：成功, -1：秒杀失败, 0： 排队中
     */
    @RequestMapping(value = "result",method =  RequestMethod.GET)
    @ResponseBody
    public Result<Long> getSeckillResult(Model model,UserVo user,@RequestParam("goodsId") long goodsId){
        model.addAttribute("user",user);
        long result = seckillService.getSeckillResult(user.getUuid(),goodsId);
        return Result.success(result);
    }

    private boolean checkPath(UserVo user,long goodsId,String path){
        if(user==null||path==null){
            return false;
        }
        String oldPath = redisService.get(SkKeyPrefix.SK_PATH,""+user.getUuid()+"_"+goodsId,String.class);
        return path.equals(oldPath);
    }
    public Result<String> getVerifyCode(HttpServletResponse response,UserVo user
    ,@RequestParam("goodsId") long goodsId){
        logger.info("获取验证码");
        if(user==null||goodsId<=0){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //刷新验证码的时候设置缓存中的随机地址无效
        String path = redisService.get(SkKeyPrefix.SK_PATH,""+user.getUuid()+"_"+goodsId,String.class);
        if(path!=null){
            redisService.delete(SkKeyPrefix.SK_PATH,""+user.getUuid()+"_"+goodsId);
        }
        //创建验证码
        try{
            VerifyCodeVo verifyCode = VerifyCodeUtil.createVerifyCode();
            //存入redis
            redisService.set(SkKeyPrefix.VERIFY_RESULT,user.getUuid()+""+goodsId,verifyCode);
            ServletOutputStream out =response.getOutputStream();
            ImageIO.write(verifyCode.getImage(),"JPEG",out);
            out.close();;
            out.flush();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(CodeMsg.SECKILL_FAIL);
        }
    }
    private String createSkPath(UserVo user, long goodsId) {
        if(user==null||goodsId<=0){
            return null;
        }
        //随机秒杀地址
        String path = MD5Util.md5(UUIDUtil.uuid()+"123456");
        //存储在reids中，不同用户不同商品的地址不一样
        redisService.set(SkKeyPrefix.SK_PATH,""+user.getUuid()+"_"+goodsId,path);
        return path;
    }


    private boolean checkVerifyCode(UserVo user,long goodsId,int verifyCode){
        if(user==null||goodsId<=0){
            return false;
        }
        //从redis中获取验证码
        Integer oldCode = redisService.get(SkKeyPrefix.VERIFY_RESULT,user.getUuid()+"_"+goodsId,Integer.class);
        if(oldCode==null||oldCode-verifyCode!=0){
            return false;
        }
        redisService.delete(SkKeyPrefix.VERIFY_RESULT,user.getUuid()+"_"+goodsId);
        return true;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goods = goodsService.ListGoodsVo();
        if(goods==null){
            return;
        }
        //库存信息存储在redis中
        for(GoodsVo good:goods){
            redisService.set(GoodsKeyPrefix.GOODS_STOCK,""+good.getId(),good.getStockCount());
            localOverMap.put(good.getId(),false);
        }
    }
}
