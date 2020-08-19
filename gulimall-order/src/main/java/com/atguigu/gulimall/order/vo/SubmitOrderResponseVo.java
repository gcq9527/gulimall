package com.atguigu.gulimall.order.vo;

import com.atguigu.gulimall.order.entity.OrderEntity;
import lombok.Data;

/**
 * 订单返回数据
 * @author Guo
 * @Create 2020/8/6 9:01
 */
@Data
public class SubmitOrderResponseVo {

    private OrderEntity order;
    /**
     * o成功 1失败
     */
    private Integer code;
}