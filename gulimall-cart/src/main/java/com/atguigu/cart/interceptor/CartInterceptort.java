package com.atguigu.cart.interceptor;

import com.atguigu.cart.vo.UserInfoTo;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.vo.MemberRespVo;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 执行目标之前 判断用户登录状态 封装传递给controller
 * @author Guo
 * @Create 2020/8/3 17:30
 */
public class CartInterceptort implements HandlerInterceptor {


    public  static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();
    /**
     * 目标方法执行之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        MemberRespVo member = (MemberRespVo) session.getAttribute(AuthServerConstant.USER_LOGIN);
        if (member != null ){
            //用户登录
            userInfoTo.setUserId(member.getId());
            userInfoTo.setTempUser(true);
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                //user-key
                String name = cookie.getName();
                if (name.equals(CartConstant.TEMP_USER_COOKIE_NAME)) {
                    userInfoTo.setUserkey(cookie.getValue());
                }
            }
        }

        //如果没有临时用户 一定要分配给临时用户
        if (userInfoTo.getUserkey() == null) {
            String s = UUID.randomUUID().toString();
            userInfoTo.setUserkey(s);
        }
        //目标方法执行之前
        threadLocal.set(userInfoTo);
        return true ;
    }

    /**
     * 业务执行之后 分配临时用户 让浏览器保存
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        //没有临时用户一定要保存一个临时用户
        if (!userInfoTo.isTempUser()) {
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserkey());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }

    }
}