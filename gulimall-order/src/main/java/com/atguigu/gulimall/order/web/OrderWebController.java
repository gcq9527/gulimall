package com.atguigu.gulimall.order.web;

import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import com.atguigu.gulimall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

/**
 * @author Guo
 * @Create 2020/8/5 11:26
 */
@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData",confirmVo);
        return "confirm";
    }

    /**
     * 下单功能
     * @param vo
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        SubmitOrderResponseVo responseVo = orderService.sumbitOrder(vo);
        //下单 去创建订单 验证令牌 验证价格 锁库存
        //下单失败回到订单确认页面重新确认订单信息
        System.out.println("订单提交的数据.." + vo);
        if (responseVo.getCode() == 0) {
            //下单成功来到支付选择页面
            model.addAttribute("submitOrderResp",responseVo);
            return "pay";
        } else {
            String msg = "下单失败:";
            switch (responseVo.getCode()) {
                case 1:msg += "订单信息过期，请刷新后提交"; break;
                case 2:msg += "订单商品价格发生变化，请确认后再次提交"; break;
                case 3:msg += "库存锁定失败，商品库存不足"; break;
            }
            redirectAttributes.addFlashAttribute("msg",msg);
            return "redirect:http://order.gulimall.com/toTrade";
        }
    }
}