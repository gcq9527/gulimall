package com.atguigu.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面所有可能传递的参数
 *
 * @author Guo
 * @Create 2020/7/27 8:55
 */
@Data
public class SearchParam {
    /**
     * 全文匹配关键字
     */
    private String keyword;

    /**
     * 三级分类
     */
    private Long catalog3Id;

    /**
     * 排血条件
     *  sort=saleCount_asc/desc 倒序
     *  sort=skuPrice_asc/desc 根据价格
     *  sort=hotScore_asc/desc
     */
    private String sort;

    /**
     * hasStock(是否有货) skuPrice区间 brandId catalog3Id attrs
     * hasStock 0/1
     * skuPrice=1_500 500_ _500
     * brandId = 1
     * attrs1_5寸_6寸
     * // 0 无库存 1有库存
     */
    private Integer hasStock ;

    /**
     * 价格区查询
     */
    private String skuPrice;

    /**
     * 多个品牌id
     */
    private List<Long> brandId;
    /**
     * 按照属性进行筛选
     */
    private List<String> attrs;
    /**
     * 页码
     */
    private Integer pageNum=1;

    /**
     * 原生所有的查询条件
     */
    private String _queryString;
}