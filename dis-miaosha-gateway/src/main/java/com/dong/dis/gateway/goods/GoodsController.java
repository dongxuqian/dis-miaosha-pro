package com.dong.dis.gateway.goods;

import com.dong.dis.api.cache.RedisServiceApi;
import com.dong.dis.api.cache.vo.GoodsKeyPrefix;
import com.dong.dis.api.goods.GoodsServiceApi;
import com.dong.dis.api.goods.vo.GoodsDetailVo;
import com.dong.dis.api.goods.vo.GoodsVo;
import com.dong.dis.api.user.vo.UserVo;
import com.dong.dis.result.Result;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping(value = "/goods/")
public class GoodsController {
    private static Logger logger = LoggerFactory.getLogger(GoodsController.class);
    @Reference(interfaceClass = RedisServiceApi.class)
    RedisServiceApi redisService;

    @Reference(interfaceClass = GoodsServiceApi.class)
    GoodsServiceApi goodsService;
    //因为在redis缓存中不存页面缓存时需要手动渲染，所以注入一个视图解析器，自定义渲染
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    /**
     *获取 SKUser 对象，并将其传递到页面解析器
     * 从数据库中获取商品信息（包含秒杀信息）
     *
     *   QPS: 1267, 用户数目5000，每个用户发起10次请求，共5000*10次请求
     *
     *  页面级缓存实现；从redis中取页面，如果没有则需要手动渲染页面，并且将渲染的页面存储在redis中供下一次访问时获取
     * @param request
     * @param response
     * @param model   响应的资源文件
     * @param user   通过自定义参数解析器UserArgumentResolver解析的 SKUser 对象
     * @return
     */
    @RequestMapping(value = "goodsList",produces = "text/html")// produces表明：这个请求会返回text/html媒体类型的数据
    @ResponseBody
    public String goodsList(HttpServletRequest request, HttpServletResponse response, Model model, UserVo user){
        logger.info("获取商品列表");
        //1.从缓存中取html
        String html = redisService.get(GoodsKeyPrefix.GOODS_LIST_HTML,"",String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        //2.如果不存在缓存，则需手动渲染
        List<GoodsVo> goodsVoList = goodsService.ListGoodsVo();
        model.addAttribute("goodsList",goodsVoList);
        model.addAttribute("user",user);
        //3.渲染html
        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        //第一个参数为渲染的文件名，第二个是web上下文
        html  = thymeleafViewResolver.getTemplateEngine().process("goods_list",webContext);
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKeyPrefix.GOODS_LIST_HTML,"",html);
        }
        return html;
    }
    /**
     * 处理商品详情页（页面静态化处理, 直接将数据返回给客户端，交给客户端处理）
     *
     * URL级缓存实现；从redis中取商品详情页面，如果没有则需要手动渲染页面，并且将渲染的页面存储在redis中供下一次访问时获取
     * 实际上URL级缓存和页面级缓存是一样的，只不过URL级缓存会根据url的参数从redis中取不同的数据
     *
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "getDetails/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> goodsDetails(UserVo user, @PathVariable("goodsId") long goodsId){
        logger.info("获取商品详情");
        //通过商品id在数据库查询
        GoodsVo goods =goodsService.getGoodsVoByGoodsId(goodsId);
        //获取商品的秒杀开始与结束时间
        long startDate = goods.getStartDate().getTime();
        long endDate = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        //秒杀状态  0: 秒杀未开始，1: 秒杀进行中，2: 秒杀已结束
        int skStatus = 0;
        //秒杀剩余时间
        int remainSeconds = 0;
        if(now<startDate){
            skStatus = 0;
            remainSeconds = (int)((startDate-now)/1000);
        }else if(now>endDate){
            skStatus =2;
            remainSeconds =-1;
        }else{
            skStatus =1;
            remainSeconds = 0;
        }
        // 服务端封装商品数据直接传递给客户端，而不用渲染页面
        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoods(goods);
        goodsDetailVo.setUser(user);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        goodsDetailVo.setSecKillStatus(skStatus);

        return Result.success(goodsDetailVo);
    }
}
