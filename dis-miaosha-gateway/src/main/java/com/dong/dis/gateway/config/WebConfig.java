package com.dong.dis.gateway.config;

import com.dong.dis.gateway.config.access.AccessInterceptor;
import com.dong.dis.gateway.config.resolver.UserArgumentResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 自定义web配置
 * @author dong
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static Logger logger = LoggerFactory.getLogger(WebConfig.class);
    @Autowired
    UserArgumentResolver userArgumentResolver;
    @Autowired
    AccessInterceptor accessInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        logger.info("添加请求拦截器");
        registry.addInterceptor(accessInterceptor);
    }

    /**
     * 添加自定义的参数解析器到MVC配置中
     * @param resolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        logger.info("添加自定义的参数解析器");
        resolvers.add(userArgumentResolver);
    }
}
