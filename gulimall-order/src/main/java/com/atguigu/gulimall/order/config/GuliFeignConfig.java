package com.atguigu.gulimall.order.config;

import com.sun.org.apache.regexp.internal.RE;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Guo
 * @Create 2020/8/5 14:45
 */
@Configuration
public class GuliFeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes attributes  = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if(attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    if (request != null ) {
                        //同步请求头数据 Cookie
                        String cookie = request.getHeader("Cookie");
                        //给新请求
                        template.header("Cookie",cookie);
                    }
                }
            }
        };
    }
}