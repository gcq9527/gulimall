package com.atguigu.gulimall.auth.fegin;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Guo
 * @Create 2020/7/31 10:29
 */
@FeignClient("gulimall-third-party")
public interface ThirdPartFeginService {


    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone,@RequestParam("codes") String codes);

}