package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Guo
 * @Create 2020/8/6 9:57
 */
@Data
public class FareVo {
    private MemberAddressVo address;
    private BigDecimal fare;
}