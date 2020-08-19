package com.atguigu.gulimall.seckill.Scheduled;

import com.atguigu.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author Guo
 * @Create 2020/8/9 14:39
 * 秒杀商品定时上架
 *  每天晚上三点 上架最近三天需要秒杀的商品
 *  当前00:00:00 - 23:59:59
 */

@Slf4j
@Service
public class SeckillSkuScheduled {

    @Autowired
    SeckillService seckillService;

    @Autowired
    RedissonClient redissonClient;

    private final String upload_lock = "seckill:upload:lock";
    @Scheduled(cron="*/10 * * * * ?")
    public void uploadSecklillSkuLatest3Days() {

        log.info("上架秒杀信息......");
        //TODO 幂等性处理
        RLock lock = redissonClient.getLock(upload_lock);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            seckillService.uploadSecklillSkuLatest3Days();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}