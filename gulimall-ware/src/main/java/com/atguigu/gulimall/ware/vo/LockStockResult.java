package com.atguigu.gulimall.ware.vo;

import lombok.Data;

/**
 * @author Guo
 * @Create 2020/8/6 11:39
 */
@Data
public class LockStockResult {

    private Long skuId;
    private Integer num;
    /**
     * 是否成功
     */
    private boolean locked;
}