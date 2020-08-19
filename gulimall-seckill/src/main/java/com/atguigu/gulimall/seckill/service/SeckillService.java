package com.atguigu.gulimall.seckill.service;

import com.atguigu.gulimall.seckill.to.SeckillSkuRedisTo;

import java.util.List;

/**
 * @author Guo
 * @Create 2020/8/9 14:46
 */
public interface SeckillService {

    public void uploadSecklillSkuLatest3Days();

    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    /**
     * 查询参加秒杀活动对象
     * @param skuId
     * @return
     */
    SeckillSkuRedisTo getSkuSeckillInfo(Long skuId);


    String kill(String killId, String key, Integer num) throws InterruptedException;

}