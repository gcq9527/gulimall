package com.atguigu.gulimall.ware.service;

import com.atguigu.common.to.mq.OrderTo;
import com.atguigu.common.to.mq.StockLockTo;
import com.atguigu.gulimall.ware.vo.LockStockResult;
import com.atguigu.gulimall.ware.vo.SkuHasStockVo;
import com.atguigu.gulimall.ware.vo.WareSkuLockVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author Guo
 * @email sunlightcs@gmail.com
 * @date 2020-07-10 13:41:33
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds);

    /**
     * 锁库存
     * @param vo
     * @return
     */
    Boolean orderLockStock(WareSkuLockVo vo);

    void unlockStock(StockLockTo to);

    /**
     * 解锁订单
     * @param orderTo
     */
    void unlockStock(OrderTo orderTo);

}

