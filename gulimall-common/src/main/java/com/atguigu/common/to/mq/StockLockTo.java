package com.atguigu.common.to.mq;

import lombok.Data;

import java.util.List;

/**
 * 库存工作单
 * @author Guo
 * @Create 2020/8/8 8:34
 */
@Data
public class StockLockTo {

    /**
     * 库存的工作单id
     */
    private Long id;
    /**
     * 工作单详情所有id
     */
    private StockDetailTo detailId;
}