package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.auth.fegin.MemberFeginService;
import com.atguigu.gulimall.auth.fegin.ThirdPartFeginService;
import com.atguigu.gulimall.auth.vo.UserLoginVo;
import com.atguigu.gulimall.auth.vo.UserRegistVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Guo
 * @Create 2020/7/30 17:49
 */
@Controller
public class LoginController {

    @Autowired
    ThirdPartFeginService thirdPartFeginService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MemberFeginService memberFeginService;


    /**
     * 短信发送
     * @param phone
     * @return
     */
    @GetMapping("/sms/sendcode")
    @ResponseBody
    public R snedCode(@RequestParam("phone") String phone) {
        Long timeOut = 60000L;
        //1.接口防刷
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        //redisCode 不为空 发送过短信
        if (!StringUtils.isEmpty(redisCode)) {
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - l < timeOut) {
                //60秒内不能再发
                return R.error(BizCodeEnume.VAILD_SMS_CODE_EXCEPTION.getCode(),BizCodeEnume.VAILD_SMS_CODE_EXCEPTION.getMsg());
            }
        }
            //随机数截取4为数字
        Random random = new Random();
        int i = random.nextInt(100000);


        String code =UUID.randomUUID().toString().substring(0, 5)+"_" + System.currentTimeMillis();
        String codes = code.substring(0,code.indexOf("_")).toUpperCase();
        //2、验证码的再次效验 redis 存key-phone value-code sms:code:13112716808
        //过期时间10分钟
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,code,10, TimeUnit.MINUTES);


        thirdPartFeginService.sendCode(phone,codes);

        return R.ok();

    }

    /**
     * //TODO 重定向携带数据 利用session原理 将数据放在session中 只要跳转到下一个页面取出数据后 session中的数据就会删除
     *
     *  分布式session问题
     * RedirectAttributes携带数据
     * @param vo
     * @param result
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

            redirectAttributes.addFlashAttribute("errors",errors);

            //效验出错 转发到注册页
            return "redirect:http://auth.gulimall.com/res.html";
        }

        //1、效验注册码
        String code = vo.getCode();

        //从redis中取出code
        String s = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if (!StringUtils.isEmpty(s)) {
            if (code.equals(vo.getCode())) {
                //删除code验证码
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
                //真正进行业务操作 远程调用
                R r = memberFeginService.regist(vo);
                if (r.getCode() == 0) {
                    //状态为0调用成功
                    return "redirect:http://auth.gulimall.com/login.html";
                } else {
                    //否则返回错误数据
                    HashMap<String, String> error = new HashMap<>(16);
                    error.put("msg",r.getData("msg",new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("errors",error);
                    return "redirect:http://auth.gulimall.com/res.html";
                }
            } else {
                //验证码与redis中数据不同
                HashMap<String, String > errors = new HashMap<>(16);
                errors.put("code","验证码错误");
                redirectAttributes.addFlashAttribute("errors",errors);
                //效验出错 转发到注册页
                return "redirect:http://auth.gulimall.com/res.html";
            }
        } else {
            //验证码为空
            HashMap<String, String> error = new HashMap<>(16);
            error.put("code","验证码为空");
            redirectAttributes.addFlashAttribute("errors",error);
            return "redirect:http://auth.gulimall.com/res.html";
        }
    }


    /**
     * 登录
     * @param vo
     * @return
     */
    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession session) {
        R login = memberFeginService.login(vo);
        if (login.getCode() == 0) {
            MemberRespVo data = login.getData("data", new TypeReference<MemberRespVo>() {
            });
            session.setAttribute(AuthServerConstant.USER_LOGIN,data);
            return "redirect:http://gulimall.com";
        } else {
            //添加错误消息到 redirect中
            Map<String,String> errors = new HashMap<>();
            errors.put("msg",login.getData("msg",new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }

    /**
     *
     * @param session
     * @return
     */
    @GetMapping("/login.html")
    public String loginPage(HttpSession session) {
        Object attribute = session.getAttribute(AuthServerConstant.USER_LOGIN);
        if (attribute == null) {
            //没登录
            return "login";
        } else {
            return "redirect:http://gulimall.com";
        }
    }
}