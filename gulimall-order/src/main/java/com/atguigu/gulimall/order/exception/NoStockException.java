package com.atguigu.gulimall.order.exception;

/**
 * @author Guo
 * @Create 2020/8/6 14:05
 */
public class NoStockException extends RuntimeException {

    private Long skuId;
    public NoStockException(Long skuId) {
        super("商品id:" + skuId + ":没有足够的库存");
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}