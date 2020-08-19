package com.atguigu.gulimall.order.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 订单提交数据
 * @author Guo
 * @Create 2020/8/5 21:32
 */
@ToString
@Data
public class OrderSubmitVo {
    /**
     * 收货地址id
     */
    private Long addrId;
    /**
     * 支付方式
     */
    private Integer payType;
    /**
     * 无需提交 去购物在获取一遍
     */
    /**
     * 防重令牌
     */
    private String orderToken;
    /**
     * 应付价格
     */
    private BigDecimal payPrice;
    /**
     * 订单备注
     */
    private String note;
}