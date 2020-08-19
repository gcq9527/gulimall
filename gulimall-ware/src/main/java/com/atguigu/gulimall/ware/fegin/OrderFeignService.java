package com.atguigu.gulimall.ware.fegin;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created by GuoChengQian on 2020/8/8 9:02
 */

@FeignClient("gulimall-order")
public interface OrderFeignService {
    /**
     * 根据订单号查询出订单实体
     * @param orderSn
     * @return
     */
    @GetMapping("/order/order/status/{orderSn}")
    public R getOrderStatus(@PathVariable("orderSn") String orderSn);
}
