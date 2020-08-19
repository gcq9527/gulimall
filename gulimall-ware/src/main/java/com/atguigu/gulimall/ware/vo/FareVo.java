package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Guo
 * @Create 2020/8/5 20:37
 */
@Data
public class FareVo {
    /**
     * 用户地址
     */
    private MemberAddressVo address;
    /**
     * 运费
     */
    private BigDecimal fare;
}