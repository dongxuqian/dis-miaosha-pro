package com.dong.dis.gateway.user;

import com.dong.dis.api.cache.vo.SkUserKeyPrefix;
import com.dong.dis.api.user.UserServiceApi;
import com.dong.dis.api.user.vo.LoginVo;
import com.dong.dis.api.user.vo.RegisterVo;
import com.dong.dis.exception.GlobalException;
import com.dong.dis.result.CodeMsg;
import com.dong.dis.result.Result;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 用户接口
 * @author dong
 */
@Controller
@RequestMapping("/user/")
public class UserController {
    private static Logger  logger = LoggerFactory.getLogger(UserController.class);
    @Reference(interfaceClass = UserServiceApi.class)
    UserServiceApi userService;
    @RequestMapping(value = "index",method = RequestMethod.GET)
    public String index(){
        logger.info("首页接口");
        return "login";
    }
    /**
     * 用户登录接口
     *
     * @param response 响应
     * @param loginVo  用户登录请求的表单数据（将表单数据封装为了一个Vo：Value Object）
     *                 注解@Valid用于校验表单参数，校验成功才会继续执行业务逻辑，否则，
     *                 请求参数校验不成功抛出异常
     * @return
     */
    @RequestMapping(value = "login",method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> login(HttpServletResponse response, @Valid LoginVo loginVo){
        String token = userService.login(loginVo);
        logger.info("token: "+token);
        //将token写入cookie中，然后传给客户端（一个cookie对应一个用户，这里将这个cookie的用户写入redis中）
        Cookie cookie  = new Cookie(UserServiceApi.COOKIE_NAME_TOKEN,token);
        cookie.setMaxAge(SkUserKeyPrefix.TOKEN.expireSeconds());//保持与redis中的session一致
        cookie.setPath("/");
        response.addCookie(cookie);
        return Result.success(true);
    }
    //跳转注册
    @RequestMapping(value = "doRegister",method = RequestMethod.GET)
    public String doRegister(){
        logger.info("doRegister");
        return "register";
    }
    //注册接口
    @RequestMapping(value = "register",method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> register(RegisterVo registerVo){
        logger.info("RegisterVo = "+registerVo);
        if(registerVo==null){
            throw new GlobalException(CodeMsg.FILL_REGISTER_INFO);
        }
        CodeMsg codeMsg = userService.register(registerVo);
        return Result.info(codeMsg);
    }
}
