package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Guo
 * @Create 2020/8/5 12:12
 */
@Data
public class OrderItemVo {
    private Long skuId;
    /**
     * 是否选中
     */
    private Boolean check = true;
    /**
     * 标题
     */
    private String title;
    /**
     * 商品图片
     */
    private String image;
    /**
     * 商品属性
     */
    private List<String> skuAttr;
    /**
     * 商品价格
     */
    private BigDecimal price;
    /**
     * 商品数量
     */
    private Integer count;
    /**
     * 总价格
     */
    private BigDecimal totalPrice;
    //TODO 待处理
    private boolean hasStock;

    /**
     * 商品重量
     */
    private BigDecimal weight;

}