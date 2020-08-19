package com.atguigu.gulimall.member.fegin;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author Guo
 * @Create 2020/8/9 10:58
 */
@FeignClient("gulimall-order")
public interface OrderFeignService {
    /**
     * 查询当前登录用户所有订单
     * @param params
     * @return
     */
    @PostMapping("/order/order/listWithItem")
    public R listWithItem(@RequestBody Map<String, Object> params);

}