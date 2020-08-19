package com.atguigu.gulimall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.order.dao.OrderItemDao;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.atguigu.gulimall.order.service.OrderItemService;


//@RabbitListener(queues = "hello-java-queue")
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 声明需要监听的所有队列
     * @RabbitHandler 根据类型来找
     */
//    @RabbitListener(queues ={"hello-java-queue"})
//    @RabbitHandler
    public void recieveMessage(Message message, OrderReturnReasonEntity content,
                               Channel channel) {
        //拿到json
        byte[] body = message.getBody();
//        JSON.parseObject(bo)
        //拿到消息头属性信息
        MessageProperties messageProperties = message.getMessageProperties();
        System.out.println("接收到·消息...内容" + content);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        //签收货物 非批量
        try {
            if (deliveryTag%2 ==0) {
                //收货
                channel.basicAck(deliveryTag, true);
                System.out.println("签收了货物.." + deliveryTag);
            } else {
                // 退货 requeue = false 丢弃 requeue=true发送会服务器 服务器重新入队

                channel.basicNack(deliveryTag,false,true);
                System.out.println("签收了货物.." + deliveryTag);
            }
        } catch (Exception e) {
            //网络中断
            e.printStackTrace();
        }
    }

//    @RabbitHandler
    public void recieveMessage2(Message message,OrderEntity orderEntity) {
        //拿到json
//        byte[] body = message.getBody();
////        JSON.parseObject(bo)
//        //拿到消息头属性信息
//        MessageProperties messageProperties = message.getMessageProperties();
        System.out.println("接收到·消息...内容" + orderEntity);
    }
}