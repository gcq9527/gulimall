package com.atguigu.gulimall.product.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created by GuoChengQian on 2020/8/10 16:24
 */
@FeignClient("gulimall-seckill")
public interface SeckillFeignService {
    /**
     * 查询当前秒杀
     * @param skuId
     * @return
     */
    @GetMapping("/sku/seckill/{skuId}")
    public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId);
}
