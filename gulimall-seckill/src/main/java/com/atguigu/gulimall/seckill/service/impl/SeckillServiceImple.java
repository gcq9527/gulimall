package com.atguigu.gulimall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.mq.SeckillOrderTo;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.seckill.feign.CouponFeignServie;
import com.atguigu.gulimall.seckill.feign.ProductFeignService;
import com.atguigu.gulimall.seckill.interceptor.LoginUserInterceptor;
import com.atguigu.gulimall.seckill.service.SeckillService;
import com.atguigu.gulimall.seckill.to.SeckillSkuRedisTo;
import com.atguigu.gulimall.seckill.to.SkuInfoVo;
import com.atguigu.gulimall.seckill.vo.SeckillSessionsWithSkusVo;
import com.atguigu.gulimall.seckill.vo.SeckillSkuVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.omg.PortableInterceptor.INACTIVE;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Guo
 * @Create 2020/8/9 14:46
 */
@Slf4j
@Service
public class SeckillServiceImple implements SeckillService {

    @Autowired
    CouponFeignServie couponFeignServie;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    RabbitTemplate rabbitTemplate;

    private final String SESSION_CACHE_PREFIX = "seckills:sessions:";
    private final String SKUKILL_CACHE_PREFIX = "seckills:skus:";
    private final String SKU_STOCK_SEMAPHORE = "seckills:stock:";

    @Override
    public void uploadSecklillSkuLatest3Days() {
        //1、扫描最近三天需要参加秒杀的活动
        R session = couponFeignServie.getLate3DaySession();
        if (session.getCode() == 0) {
            //上架商品
            List<SeckillSessionsWithSkusVo> sessionData = session.getData(new TypeReference<List<SeckillSessionsWithSkusVo>>() {
            });
            if (!CollectionUtils.isEmpty(sessionData)) {
                SeckillSessionsWithSkusVo seckillSessionsWithSkusVo = sessionData.get(0);
                System.out.println(seckillSessionsWithSkusVo.getStartTime());
                //缓存到redis
                //1、缓存活动信息
                saveSessionInfos(sessionData);

                //2、缓存活动的管理商品信息
                saveSessionSkuInfos(sessionData);
            }

        }
    }

    /**
     * 返回当前时间可以参与秒伤商品信息
     * @return
     */
    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        //1、确定当前时间属于那个秒杀场次
        //1970
        long time = new Date().getTime();
        Set<String> keys = redisTemplate.keys(  SESSION_CACHE_PREFIX + "*");
        for(String key : keys) {
            String replace = key.replace(SESSION_CACHE_PREFIX, "");
            String[] s = replace.split("_");
            Long start = Long.parseLong(s[0]);
            Long end = Long.parseLong(s[1]);
            if (time >= start && time <= end) {

            }
            if (true) {
                //2、获取这个秒杀场次需要的所有商品信息
                List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                BoundHashOperations<String,String,String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                List<String> list = hashOps.multiGet(range);
                if (list != null ) {
                    List<SeckillSkuRedisTo> collect = list.stream().map(item -> {
                        SeckillSkuRedisTo redis = JSON.parseObject((String) item, SeckillSkuRedisTo.class);
                        return redis;
                    }).collect(Collectors.toList());
                    return collect;
                }
                break;
            }
        }
        //2、获取这个秒杀场次需要的所有商品信息
        return null;
    }

    @Override
    public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) {
        //1、找到所有需要参与秒杀的商品的key
        BoundHashOperations<String,String,String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        Set<String> keys = hashOps.keys();
        if (keys != null && keys.size() > 0 ) {
            String regs = "\\d_" + skuId;
            for (String key : keys) {
                if (Pattern.matches(regs,key)) {
                    String s = hashOps.get(key);
                    SeckillSkuRedisTo redisTo = JSON.parseObject(s, SeckillSkuRedisTo.class);

                    //随机码
                    long current = new Date().getTime();
                    Long startTime = redisTo.getStartTime();
                    if (current>=redisTo.getStartTime() && current<=redisTo.getEndTime()) {

                    } else {
                        redisTo.setRandomCode(null);
                    }
                    return redisTo;
                }
            }
        }

        return null;
    }

    /**
     * 秒杀
     * @param killId
     * @param key
     * @param num
     * @return
     * @throws InterruptedException
     */
    @Override
    public String kill(String killId, String key, Integer num) throws InterruptedException {
        MemberRespVo respVo = LoginUserInterceptor.loginUser.get();
        //1、获取当前秒杀商品的详细信息
        BoundHashOperations<String,String,String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        String json = hashOps.get(killId);
        if (StringUtils.isEmpty(json)) {
            return null;
        } else {
            //转换为对象
            SeckillSkuRedisTo redisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);
            //效验合法性 拿到秒杀商品和当前时间进行比较
            Long startTime = redisTo.getStartTime();
            Long endTime = redisTo.getEndTime();
            long time = new Date().getTime();
            long ttl = endTime - time;
            //1、效验时间的合法性
            if (time >= startTime && time <= endTime) {
                //2、效验随机码和商品id
                String randomCode = redisTo.getRandomCode();
                String skuId = redisTo.getPromotionSessionId() + "_" + redisTo.getSkuId();
                if (randomCode.equals(key) && killId.equals(skuId)) {
                    //3、数量是否合理
                    if (num <= redisTo.getSeckillLimit()) {
                        //4、验证这个人是否以及购买 幂等性 如果只要秒杀成功 就去占位
                        //SETNX
                        String redisKey = respVo.getId() + "_" + skuId;
                        //zidong1
                        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(),ttl, TimeUnit.MICROSECONDS);
                        if (aBoolean) {
                            //占位成功 可以购物
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);

                                boolean b = semaphore.tryAcquire(num);
                                if (b) {
                                    //秒杀成功
                                    //快速下单
                                    String timeId = IdWorker.getTimeId();
                                    SeckillOrderTo orderTo = new SeckillOrderTo();
                                    orderTo.setOrderSn(timeId);
                                    orderTo.setMemberId(respVo.getId());
                                    orderTo.setNum(num);
                                    orderTo.setPromotionSessionId(redisTo.getPromotionSessionId());
                                    orderTo.setSeckillPrice(redisTo.getSeckillPrice());
                                    rabbitTemplate.convertAndSend("order-event-exchange","order.seckill.order",orderTo);
                                    long s2 = System.currentTimeMillis();
                                    return timeId;
                                }
                                return null;
                          } else {
                            //占位失败不可以购买
                            return null;
                        }
                    }
                } else {
                    return null;
                }
            }

        }

        return null;
    }

    private void saveSessionInfos(List<SeckillSessionsWithSkusVo> sessionData) {
        sessionData.forEach(session -> {
            long startTime = session.getStartTime().getTime();
            long endTime = session.getEndTime().getTime();
            String key = SESSION_CACHE_PREFIX + startTime + "_" + endTime;
            Boolean hasKey = redisTemplate.hasKey(key);
            if (!hasKey) {
                List<String> collect = session.getRelationSkus().stream()
                        .map(item -> item.getPromotionSessionId() + "_" + item.getSkuId().toString()).collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(key, collect);
            }
        });
    }



    private void saveSessionSkuInfos(List<SeckillSessionsWithSkusVo> session) {
        session.stream().forEach(sessions -> {
            //准备hash操作
            BoundHashOperations ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            sessions.getRelationSkus().stream().forEach(seckillSkuVo -> {
                String token = UUID.randomUUID().toString().replace("-","");

                if(!ops.hasKey(seckillSkuVo.getPromotionSessionId() + "_" + seckillSkuVo.getSkuId().toString())) {
                    SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
                    //1、sku的基本数据
                    R skuInfo = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                    if (skuInfo.getCode() == 0) {
                        SkuInfoVo info = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                        });
                        redisTo.setSkuInfoVo(info);
                    }
                    //2、sku的秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo, redisTo);

                    //3、设置上当前商品秒杀时间信息
                    redisTo.setStartTime(sessions.getStartTime().getTime());
                    redisTo.setEndTime(sessions.getEndTime().getTime());

                    //4、随机码
                    redisTo.setRandomCode(token);

                    //保存到redis
                    String jsonString = JSON.toJSONString(redisTo);
                    ops.put(seckillSkuVo.getPromotionSessionId() + "_" + seckillSkuVo.getSkuId().toString(), jsonString);

                    //如果当前这个
                    //使用库存作为分布式的信号量 限流
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    if(semaphore != null) {
                        //商品秒杀数量作为信号
                        semaphore.trySetPermits(seckillSkuVo.getSeckillCount());
                    }
                }
            });
        });
    }
}