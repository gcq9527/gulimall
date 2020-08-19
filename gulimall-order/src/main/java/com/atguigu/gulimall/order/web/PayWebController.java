package com.atguigu.gulimall.order.web;

import com.alipay.api.AlipayApiException;
import com.atguigu.gulimall.order.config.AlipayTemplate;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Guo
 * @Create 2020/8/8 22:48
 */
@Controller
public class PayWebController {

    @Autowired
    AlipayTemplate alipayTemplate;

    @Autowired
    OrderService orderService;

    /**
     * 用户跳转到支付页面
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    @ResponseBody
    @GetMapping(value = "/payOrder",produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
//        PayVo payVo = new PayVo();
     /*   //订单备注
        payVo.setBody();
        //订单号
        payVo.setOut_trade_no();
        //订单主题
        payVo.setSubject();
        //订单金额
        payVo.setTotal_amount();*/
        PayVo payVo = orderService.getOrderPay(orderSn);
        return  alipayTemplate.pay(payVo);
    }

    
}