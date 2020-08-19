/**
 * Copyright 2019 bejson.com
 */
package com.atguigu.gulimall.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2019-11-26 10:50:34
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class SpuSaveVo {

    /**
     * 商品名称
     */
    private String spuName;
    /**
     * spu描述信息
     */
    private String spuDescription;
    /**
     * 分组id
     */
    private Long catalogId;
    /**
     *  品牌id
     */
    private Long brandId;
    /**
     * 重量
     */
    private BigDecimal weight;
    /**
     * 上架状态 0上架 1下架
     */
    private int publishStatus;
    /**
     *  图片描述信息
     */
    private List<String> decript;
    /**
     * 图片集合
     */
    private List<String> images;
    /**
     *  积分信息
     */
    private Bounds bounds;
    /**
     * 基本属性
     */
    private List<BaseAttrs> baseAttrs;
    /**
     *
     */
    private List<Skus> skus;



}