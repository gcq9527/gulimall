package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Created by GuoChengQian on 2020/7/23 14:22
 */
@FeignClient("gulimall-search")
public interface SearchFeignService {

    @PostMapping("search/save/product")
     R productStatusUp(@RequestBody List<SkuEsModel> skuEsModel);

}