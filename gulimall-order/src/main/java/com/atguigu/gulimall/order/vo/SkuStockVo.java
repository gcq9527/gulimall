package com.atguigu.gulimall.order.vo;

import lombok.Data;

/**
 * @author Guo
 * @Create 2020/8/5 17:01
 */
@Data
public class SkuStockVo {
    private Long skuId;
    private Boolean hasStock;
}