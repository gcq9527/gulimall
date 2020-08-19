package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.auth.fegin.MemberFeginService;
import com.atguigu.gulimall.auth.vo.SociaIUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Guo
 * @Create 2020/8/1 13:07
 */
@Slf4j
@Controller
public class Oauth2Controller {

    @Autowired
    MemberFeginService memberFeginService;

    /**
     * 社交登录成功回调
     * @param code
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/oauth2.0/weibo/success",produces = "application/json;utf-8")
    public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("client_id", "1133714539");
        map.put("client_secret", "f22eb330342e7f8797a7dbe173bd9424");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://auth.gulimall.com/oauth2.0/weibo/success");
        map.put("code", code);

        HttpResponse response = HttpUtils.doPost("https://api.weibo.com",
                "/oauth2/access_token",
                "post",
                new HashMap<>(),
                map,
                new HashMap<>());

        if(response.getStatusLine().getStatusCode() == 200) {
            //获取到了accessToken
            //知道当前是那个社交用户
            //1)、当前用户如果第一次点击网站 自动注册进来(为当前社交用户生成一个会员信息账号，以后这个社交账号对应这个用户)
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(response.getEntity());
            SociaIUser sociaIUser = JSON.parseObject(json, SociaIUser.class);

            R r = memberFeginService.oauthlogin(sociaIUser);
            if (r.getCode() == 0) {
                MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {
                });
                log.info("登陆成功：用户{}",data);
                //1、第一次使用session 命令浏览器保存卡号 JSESSIONID这个cokie
                //以后浏览器访问那个网站都会带上这个网站的cookie
                //子域之间 gulimall.com auth.gulimall.com
                //发卡的时候 指定域名为父域名 即使是子域系统发的卡 也能让父域直接使用
                //2、登录成功就跳回首页
                // TODO 1、默认发的令牌 session=dsajkdjl 作用域当前域(解决子域问题)
                // TODO 2、使用JSON序列化方式来序列化对象存到redis中
                session.setAttribute(AuthServerConstant.USER_LOGIN,data);
                return "redirect:http://gulimall.com";
            } else {
                return "redirect:http://auth.gulimall.com/login.html";
            }

        } else {
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }
}