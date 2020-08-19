package com.atguigu.gulimall.order.service;

import com.atguigu.common.to.mq.SeckillOrderTo;
import com.atguigu.gulimall.order.vo.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.order.entity.OrderEntity;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author Guo
 * @email sunlightcs@gmail.com
 * @date 2020-07-10 13:36:20
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 订单确认页返回需要的数据
     * @return
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    SubmitOrderResponseVo sumbitOrder(OrderSubmitVo vo);

    /**
     * 根据订单号 查询订单对象
     * @param orderSn
     * @return
     */
    OrderEntity getOrderByOrderSn(String orderSn);


    void closeOrder(OrderEntity orderEntity);

    /**
     * 获取当前订单支付信息
     * @param orderSn
     * @return
     */
    PayVo getOrderPay(String orderSn);

    /**
     * 分页查询
     * @param params
     * @return
     */
    PageUtils queryPageWithItem(Map<String, Object> params);

    String handlePayResult(PayAsyncVo vo );

    /**
     * 秒杀
     * @param entity
     */
    void createSeckillOrder(SeckillOrderTo entity);
}

