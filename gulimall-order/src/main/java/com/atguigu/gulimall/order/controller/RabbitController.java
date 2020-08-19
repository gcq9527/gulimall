package com.atguigu.gulimall.order.controller;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

/**
 * @author Guo
 * @Create 2020/8/4 19:58
 */
@Slf4j
@RestController
public class RabbitController {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/test/createOrder")
    public String createOrderTest() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(UUID.randomUUID().toString());
        orderEntity.setModifyTime(new Date());

        //给MQ发送消息
        rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",orderEntity);

        return "ok";
    }

    @GetMapping("/send")
    public String sendMq(@RequestParam(value = "num",defaultValue = "10") Integer num) {
        for (int i = 0; i<num; i++) {
            if (i%2==0) {
                OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
                reasonEntity.setId(1L);
                reasonEntity.setCreateTime(new Date());
                reasonEntity.setName("哈哈");
                //发送消息
                String msg = "hello Wrold";
                rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",reasonEntity,new CorrelationData(UUID.randomUUID().toString()));
                log.info("消息发送完成");
            } else{
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setBillContent("testtest");
                rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",orderEntity,new CorrelationData(UUID.randomUUID().toString()));
                log.info("消息发送完成");
            }
        }
        return "ok";
    }
}