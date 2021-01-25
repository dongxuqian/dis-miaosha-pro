package com.dong.dis.gateway.config.resolver;

import com.dong.dis.api.cache.RedisServiceApi;
import com.dong.dis.api.cache.vo.SkUserKeyPrefix;
import com.dong.dis.api.user.UserServiceApi;
import com.dong.dis.api.user.vo.UserVo;
import com.dong.dis.gateway.config.access.UserContext;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 解析请求，并将请求的参数设置到方法参数中
 */
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    private Logger logger = LoggerFactory.getLogger(UserArgumentResolver.class);
    @Reference(interfaceClass = RedisServiceApi.class)
    RedisServiceApi redisService;

    /**
     * 当请求参数为 UserVo 时，使用这个解析器处理
     * 客户端的请求到达某个 Controller 的方法时，判断这个方法的参数是否为 UserVo，
     * 如果是，则这个 UserVo 参数对象通过下面的 resolveArgument() 方法获取，
     * 然后，该 Controller 方法继续往下执行时所看到的 UserVo 对象就是在这里的 resolveArgument() 方法处理过的对象
     *
     * @param methodParameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        logger.info("supportsParameter");
        Class<?> parameterType = methodParameter.getParameterType();
        return parameterType== UserVo.class;
    }
    /**
     * 从分布式 session 中获取 UserVo 对象
     *
     * @param methodParameter
     * @param modelAndViewContainer
     * @param nativeWebRequest
     * @param webDataBinderFactory
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
/**
 * threadlocal 存储 线程副本  保证线程不冲突。
 */
        //        //请求和响应对象
//        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
//        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
//        logger.info(request.getRequestURL()+" resolveArgument");
//        //从请求对象中获取token（有两种方式从客户端返回1.url参数2.set-cookie字段）
//        String paramToken  = request.getParameter(UserServiceApi.COOKIE_NAME_TOKEN);
//        String cookieToken = getCookieValue(request,UserServiceApi.COOKIE_NAME_TOKEN);
//        if(StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken)){
//            return null;
//        }
//        //判断是哪种方式返回的token
//        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
//        UserVo userVo = redisService.get(SkUserKeyPrefix.TOKEN,token,UserVo.class);
        UserVo  userVo = UserContext.getUser();

        logger.info("获取userVo: "+userVo.toString());
        // 在有效期内从redis获取到key之后，需要将key重新设置一下，从而达到延长有效期的效果
//        if(userVo!=null){
//            addCookie(response,token,userVo);
//        }
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
