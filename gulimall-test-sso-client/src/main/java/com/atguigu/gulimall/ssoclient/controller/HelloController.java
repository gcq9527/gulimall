package com.atguigu.gulimall.ssoclient.controller;

import com.sun.corba.se.impl.oa.toa.TOA;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Guo
 * @Create 2020/8/3 9:46
 */
@Controller
public class HelloController {

    @Value("${sso.server.url}")
    String ssoServerUrl;
    /**
     * 无需登录就可以访问
     * @return
     */
    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    /**
     *
     * @param model
     * @param session
     * @param token 登陆成功携带token
     * @return
     */
    @GetMapping("/employess")
    public String employess(Model model, HttpSession session, @RequestParam(value = "token",required = false) String token) {
        //session中没有携带值 未登录

        if (!StringUtils.isEmpty(token)) {
            //登陆成功就会携带toen
            //但是token是那个用户
            //TODO 1、去ssoServer 获取当前token真正的用户信息
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> forEntity = restTemplate.getForEntity("http://ssoserver.com:8080/userInfo?token=" + token, String.class);
            String body = forEntity.getBody();
            session.setAttribute("loginUser",body);
        }
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) {
            //没登陆
            return "redirect:" + ssoServerUrl + "?redirect_url=http://client1.com:8081/employess";
        } else {
            List<String> emps = new ArrayList<>();
            emps.add("张三");
            emps.add("李四");
            model.addAttribute("emps", emps);
            return "list";
        }
    }

}