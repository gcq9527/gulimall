package com.atguigu.cart.config;

import com.atguigu.cart.interceptor.CartInterceptort;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Guo
 * @Create 2020/8/3 17:49
 */
@Configuration
public class GulimallWebConfig implements WebMvcConfigurer {

    /**
     * 拦截请求
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CartInterceptort()).addPathPatterns("/**");
    }
}