package com.atguigu.gulimall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Guo
 * @Create 2020/8/5 12:07
 */

public class OrderConfirmVo {

    /**
     * 是否有库存
     */
    @Getter @Setter
    Map<Long,Boolean> stocks;
    /**
     * 收货地址
     */
    @Setter @Getter
    List<MemberAddressVo> address;
    /**
     * 所有选中购物项
     */
    @Setter @Getter
    List<OrderItemVo> items;

    //防重令牌
    @Setter @Getter
    String orderToken;

    //发票记录

    public Integer getCount() {
        Integer i = 0;
        if (items != null) {
            for (OrderItemVo item:
                 items) {
                i+=item.getCount();
            }
        }
        return i;
    }
    //优惠卷
    @Setter @Getter
    Integer integration;
    /**
     * 订单总额
     */
    BigDecimal c;
    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if (items != null) {
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                sum = sum.add(multiply);
            }
        }
        return sum;
    }
    /**
     * 应付价格
     */
    BigDecimal payPrice;
    public BigDecimal getPayPrice() {
       return getTotal();
    }
}