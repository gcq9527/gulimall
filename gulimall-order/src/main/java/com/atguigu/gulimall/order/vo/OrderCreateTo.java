package com.atguigu.gulimall.order.vo;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Guo
 * @Create 2020/8/6 9:49
 */
@Data
public class OrderCreateTo {
    /**
     * 订单
     */
    private OrderEntity order;
    /**
     * 订单项信息
     */
    private List<OrderItemEntity> orderItems;
    /**
     * 订单计算应付价格
     */
    private BigDecimal payPrice;
    /**
     * 运费
     */
    private BigDecimal fare;

}