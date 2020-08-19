package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 积分
 * @author Guo
 * @Create 2020/7/18 20:03
 */
@Data
public class SpuBoundTo {
    private Long spuId;
    /**
     * 成长积分
     */
    private BigDecimal growBounds;
    /**
     * 购物积分
     */
    private BigDecimal buyBounds;

}