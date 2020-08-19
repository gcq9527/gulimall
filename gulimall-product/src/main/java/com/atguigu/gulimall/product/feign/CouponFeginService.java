package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by GuoChengQian on 2020/7/18 19:57
 */
@FeignClient("gulimall-coupon")
public interface CouponFeginService {

    /**
     * 1.CouponFeingService.saveSpuBounds(spuBoundTo)
     *      1)@RequestBody 将这个对象转为json
     *      2)找到gulimall-coupon服务 给/coupon/spubounds/save 发送请求
     *          将上一步转的json放在请求的位置 发送请求
     *      3) 对方的服务收到请求 请求体里有json数据
     *          (@RequestBody SpuBoundEntity spuBounds) 将请求体的json转为SpuBoundEntity
     *  只要json数据模型是兼容的 双方服务无需使用同一个to
     * @param spuBoundTo
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    /**
     * 保存sku信息
     * @param skuReductionTo
     */
    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
