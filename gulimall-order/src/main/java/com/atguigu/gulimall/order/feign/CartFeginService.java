package com.atguigu.gulimall.order.feign;

import com.atguigu.gulimall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author Guo
 * @Create 2020/8/5 13:04
 */
@FeignClient("gulimall-cart")
public interface CartFeginService {

    /**
     * 查询购物车内容
     * @return
     */
    @GetMapping("/currentUserCartItems")
    public List<OrderItemVo> getCurrentUserCartItems();
}