package com.atguigu.gulimall.seckill.controller;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.seckill.service.SeckillService;
import com.atguigu.gulimall.seckill.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Guo
 * @Create 2020/8/10 10:52
 */
@Controller
public class SeckillController {

    @Autowired
    SeckillService seckillService;

    @GetMapping("/currentSeckillSkus")
    @ResponseBody
    public R getCurrentSeckillSkus() {
         List<SeckillSkuRedisTo> list = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(list);
    }

    @GetMapping("/sku/seckill/{skuId}")

    @ResponseBody
    public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId) {
        SeckillSkuRedisTo redisTo =  seckillService.getSkuSeckillInfo(skuId);
        return R.ok().setData(redisTo);
    }

    /**
     * 秒杀
     * @param killId
     * @param key
     * @param num
     * @param model
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/kill")
    public String seckill(@RequestParam("killId") String killId,
                     @RequestParam("key") String key,
                     @RequestParam("num") Integer num,
                     Model model) throws InterruptedException {

        String orderSn = seckillService.kill(killId,key,num);
        model.addAttribute("orderSn",orderSn);
        //判断用户是否登录
        return "index";
    }
}