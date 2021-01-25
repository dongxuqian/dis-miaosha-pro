package com.dong.dis.gateway.order;

import com.dong.dis.api.goods.GoodsServiceApi;
import com.dong.dis.api.goods.vo.GoodsVo;
import com.dong.dis.api.order.OrderServiceApi;
import com.dong.dis.api.order.vo.OrderDetailVo;
import com.dong.dis.api.user.vo.UserVo;
import com.dong.dis.domain.OrderInfo;
import com.dong.dis.result.CodeMsg;
import com.dong.dis.result.Result;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 订单服务
 * @author dong
 */
@Controller
@RequestMapping(value = "/order/")
public class OrderController {
    @Reference(interfaceClass = OrderServiceApi.class)
    OrderServiceApi orderService;
    @Reference(interfaceClass = GoodsServiceApi.class)
    GoodsServiceApi goodsService;
    @ResponseBody
    @RequestMapping("detail")
    public Result<OrderDetailVo> orderInfo(Model model, UserVo user, @RequestParam("orderId") long orderId){
        if(user==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if(order==null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setUser(user);
        vo.setOrder(order);
        vo.setGoods(goods);
        return Result.success(vo);
    }
}
