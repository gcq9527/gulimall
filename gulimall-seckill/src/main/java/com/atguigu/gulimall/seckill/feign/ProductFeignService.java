package com.atguigu.gulimall.seckill.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by GuoChengQian on 2020/8/9 16:47
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {
    /**
     * 信息
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    //@RequiresPermissions("product:skuinfo:info")
    public R getSkuInfo(@PathVariable("skuId") Long skuId);

}
