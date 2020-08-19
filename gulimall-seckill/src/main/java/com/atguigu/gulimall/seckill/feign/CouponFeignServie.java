package com.atguigu.gulimall.seckill.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Guo
 * @Create 2020/8/9 14:48
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignServie {

    /**
     * 查询所有秒杀商品
     * @return
     */
    @GetMapping("/coupon/seckillsession/lates3DaySession")
    public R getLate3DaySession();
}