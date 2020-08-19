package com.atguigu.gulimall.order.feign;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Guo
 * @Create 2020/8/5 16:59
 */
@FeignClient("gulimall-ware")
public interface WmsFeignService {

    @PostMapping("ware/waresku/hasstock")
    public R getSkusHasStock(@RequestBody List<Long> skuIds);

    /**
     * 根据adrid查询用户信息以及地址
     * @param addrId
     * @return
     */
    @GetMapping("ware/wareinfo/fare")
    public R getFare(@RequestParam("addrId") Long addrId);


    /**
     * 为某个订单锁定库存
     * @param vo
     * @return
     */
    @PostMapping("ware/waresku/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo vo);

}