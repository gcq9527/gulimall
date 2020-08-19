package com.atguigu.gulimall.search.fegin;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 远程调用product服务
 * @author Guo
 * @Create 2020/7/29 8:49
 */
@FeignClient("gulimall-product")
public interface ProductFeginService {

    @RequestMapping("product/attr/info/{attrId}")
    public R getAttrInfo(@PathVariable("attrId") Long attrId);

    @GetMapping("product/brand/infos")
    public R brandInfo(@RequestParam("brandIds") List<Long> brandIds);
}