package com.dong.dis.result;

import java.io.Serializable;

/**
 * 响应结果状态码
 * @author dong
 */
public class CodeMsg implements Serializable {
    private int code;
    private String msg;
    /**
     * 通用异常
     */
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session不存在或者已经失效，请返回登录！");
    public static CodeMsg ACCESS_LIMIT_REACHED = new CodeMsg(500104, "访问太频繁！");
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常：%s");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "手机号不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "密码错误");
    public static CodeMsg WAIT_REGISTER_DONE = new CodeMsg(500220, "等待注册完成");
    public static CodeMsg USER_EXIST = new CodeMsg(500216, "用户已经存在，无需重复注册");
    public static CodeMsg REGISTER_FAIL = new CodeMsg(500218, "注册异常");
    public static CodeMsg FILL_REGISTER_INFO = new CodeMsg(500219, "请填写注册信息");
    public static CodeMsg SECKILL_PARM_ILLEGAL = new CodeMsg(500503, "秒杀请求参数异常：%s");
    public static CodeMsg VERITF_FAIL = new CodeMsg(500103, "校验失败，请重新输入表达式结果或刷新校验码重新输入");
    public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(500102, "请求非法");
    public static CodeMsg SECKILL_OVER = new CodeMsg(500500, "商品已经秒杀完毕");
    public static CodeMsg REPEATE_SECKILL = new CodeMsg(500501, "不能重复秒杀");
    public static CodeMsg SECKILL_FAIL = new CodeMsg(500502, "秒杀失败");
    public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400, "订单不存在");


    public CodeMsg(int code, String msg){
        this.code = code;
        this.msg  = msg;
    }
    public CodeMsg fillArgs(Object... args){
        int code = this.code;
        String message = String.format(this.msg,args);
        return new CodeMsg(code,message);
    }

    public int getCode() {
        return code;
    }
    public String getMsg(){
        return msg;
    }
}
