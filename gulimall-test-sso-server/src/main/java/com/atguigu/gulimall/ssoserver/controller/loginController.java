package com.atguigu.gulimall.ssoserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author Guo
 * @Create 2020/8/3 10:04
 */
@Controller
public class loginController {

    @Autowired
    StringRedisTemplate redisTemplate;

    @ResponseBody
    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("token") String token) {
        String s = redisTemplate.opsForValue().get(token);
        return s;
    }

    @GetMapping("login.html")
    public String login(@RequestParam("redirect_url") String url, Model model,
                        @CookieValue(value = "sso_token",required = false) String sso_token){
        if (!StringUtils.isEmpty(sso_token)) {
            //说明之前登录 浏览器留下痕迹
            return "redirect:" + url + "?token=" + sso_token;
        }
        model.addAttribute("url",url);

        return "login";
    }

    @PostMapping("/login")
    public String deLogin(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam("url") String url,
                          HttpServletResponse response) {
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            //登陆成功
            //登录的用户保存到reids中
            String uuid = UUID.randomUUID().toString().replace("-", "");
            redisTemplate.opsForValue().set(uuid,username);

            Cookie sso_token = new Cookie("sso_token", uuid);
            response.addCookie(sso_token);
            return "redirect:" + url + "?token=" + uuid;
        }
        //登录失败 重新跳转涛登陆页面
        return "login";
    }
}