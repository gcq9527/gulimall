package com.atguigu.cart.fegin;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Guo
 * @Create 2020/8/3 21:34
 */
@FeignClient("gulimall-product")
public interface ProductFeginService {

    @RequestMapping("product/skuinfo/info/{skuId}")
    public R getSkuInfo(@PathVariable("skuId") Long skuId);


    @GetMapping("product/skusaleattrvalue/stringlist/{skuId}")
    public List<String> getSkuSaleAttrValue(@PathVariable("skuId") long skuId);


    /**
     * 根据skuid查询出skuInfo对象 返回价格
     * @param skuId
     * @return
     */
    @GetMapping("product/skuinfo/{skuId}/price")
    public R getPrice(@PathVariable("skuId") Long skuId);
}