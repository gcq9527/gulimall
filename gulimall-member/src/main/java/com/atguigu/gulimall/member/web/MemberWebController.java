package com.atguigu.gulimall.member.web;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.member.fegin.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

/**
 * @author Guo
 * @Create 2020/8/9 10:22
 */
@Controller
public class MemberWebController {

    @Autowired
    OrderFeignService orderFeignService;


    @GetMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, Model model) {
        //查询出当前登录的用户的所有订单数据
        HashMap<String, Object> page = new HashMap<>();
        page.put("page",pageNum.toString());
        R r = orderFeignService.listWithItem(page);
        System.out.println(JSON.toJSONString(r));
        model.addAttribute("orders",r);
       return "index";
    }
}