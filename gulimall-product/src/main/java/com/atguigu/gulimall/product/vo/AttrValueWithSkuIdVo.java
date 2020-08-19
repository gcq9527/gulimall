package com.atguigu.gulimall.product.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @author Guo
 * @Create 2020/7/30 13:05
 */
@Data
@ToString
public class AttrValueWithSkuIdVo {
    private String attrValue;
    private String skuIds;
}