package com.atguigu.gulimall.thirdparty.controller;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.thirdparty.commponent.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Guo
 * @Create 2020/7/31 10:31
 */
@RestController
@RequestMapping("/sms")
public class SmsController {

    @Autowired
    SmsComponent smsComponent;

    @GetMapping("/sendCode")
    public R sendCode(@RequestParam("phone") String phone,@RequestParam("codes") String codes) {
        smsComponent.sendsmsCode(phone,codes);
        return R.ok();
    }
}