package com.dong.dis.gateway.config.access;

import com.alibaba.fastjson.JSON;
import com.dong.dis.api.cache.RedisServiceApi;
import com.dong.dis.api.cache.vo.AccessKeyPrefix;
import com.dong.dis.api.cache.vo.SkUserKeyPrefix;
import com.dong.dis.api.user.UserServiceApi;
import com.dong.dis.api.user.vo.UserVo;
import com.dong.dis.result.CodeMsg;
import com.dong.dis.result.Result;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * 用户访问拦截器
 */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {
    Logger logger = LoggerFactory.getLogger(AccessInterceptor.class);
    @Reference(interfaceClass = UserServiceApi.class)
    UserServiceApi userService;
    @Reference(interfaceClass = RedisServiceApi.class)
    RedisServiceApi redisService;

    /**
     * 目标方法执行前的处理
     * 查询访问次数，进行防刷请求拦截
     * 在 AccessLimit#seconds() 时间内频繁访问会有次数限制
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info(request.getRequestURL()+"拦截请求");
        //指明拦截的是方法
        if(handler instanceof HandlerMethod){
            logger.info("HandlerMethod: "+((HandlerMethod) handler).getMethod().getName());
            UserVo user =this.getUser(request,response);
            //保存用户到ThreadLocal，这样，同一个线程访问的是同一个用户
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit ==null){
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxAccessCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if(needLogin){
                if(user==null){
                    this.render(response,CodeMsg.SESSION_ERROR);
                    return false;
                }
                key+="_"+user.getPhone();
            }else{

            }
            AccessKeyPrefix accessKeyPrefix = AccessKeyPrefix.withExpire(seconds);
            Integer count = redisService.get(accessKeyPrefix,key,Integer.class);
            if(count==null){
                redisService.set(accessKeyPrefix,key,1);
            }else if(count<maxCount){
                redisService.incr(accessKeyPrefix,key);
            }else{
                this.render(response,CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }

        }
        return true;
    }

    /**
     * 渲染返回信息
     * 以json格式返回
     * @param response
     * @param cm
     * @throws Exception
     */
    public void render(HttpServletResponse response, CodeMsg cm)throws Exception{
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out  = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(cm));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }
    private UserVo getUser(HttpServletRequest request,HttpServletResponse response){
        logger.info(request.getRequestURL()+"获取UserVo 对象");
        // 从请求中获取token
        String paramToken = request.getParameter(UserServiceApi.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, UserServiceApi.COOKIE_NAME_TOKEN);

        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }

        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;

        if (StringUtils.isEmpty(token)) {
            return null;
        }

        UserVo userVo = redisService.get(SkUserKeyPrefix.TOKEN, token, UserVo.class);

        // 在有效期内从redis获取到key之后，需要将key重新设置一下，从而达到延长有效期的效果
        if (userVo != null) {
            addCookie(response, token, userVo);
        }
        return userVo;
    }
    private String getCookieValue(HttpServletRequest request,String cookieName){
        logger.info("getCookieValue");
        Cookie[] cookies = request.getCookies();
        if(cookies==null||cookies.length==0){
            logger.info("cookie is null");
            return null;
        }
        for(Cookie cookie:cookies){
            if(cookie.getName().equals(cookieName)){
                return cookie.getValue();
            }
        }
        return null;
    }
    private void addCookie(HttpServletResponse response,String token,UserVo user){
        redisService.set(SkUserKeyPrefix.TOKEN,token,user);
        Cookie cookie = new Cookie(UserServiceApi.COOKIE_NAME_TOKEN,token);
        cookie.setMaxAge(SkUserKeyPrefix.TOKEN.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
