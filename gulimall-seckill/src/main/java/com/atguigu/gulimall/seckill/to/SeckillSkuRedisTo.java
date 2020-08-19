package com.atguigu.gulimall.seckill.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Guo
 * @Create 2020/8/9 16:41
 */
@Data
public class SeckillSkuRedisTo {

    private Long id;
    /**
     * 活动id
     */
    private Long promotionId;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private BigDecimal seckillCount;
    /**
     * 每人限购数量
     */
    private Integer seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;

    //sku的详细信息
    private SkuInfoVo skuInfoVo;

    /**
     * 开始时间
     */
    private Long startTime;
    /**
     * 结束时间
     */
    private Long endTime;
    /**
     * 商品秒杀随机码
     */
    private String randomCode;
}