package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.util.List;

/**
 * 锁库存
 * @author Guo
 * @Create 2020/8/6 11:36
 */
@Data
public class WareSkuLockVo {

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 需要锁住的所有库存信息
     */
    private List<OrderItemVo> locks;

}