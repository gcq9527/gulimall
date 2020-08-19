package com.atguigu.gulimall.search.vo;

import com.atguigu.common.to.es.SkuEsModel;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品查询返回信息
 * @author Guo
 * @Create 2020/7/27 9:09
 */
@Data
@ToString
public class SearchResult {
    /**
     * 查询到的商品信息
     */
    private List<SkuEsModel> products;
    /**
     * 当前页码
     */
    private Integer pageNum;
    /**
     * 总记录数
     */
    private Long total;
    /**
     * 总页码
     */
    private Integer totalPages;

    /**
     * 当前查询到的结果，所有涉及到的品牌
     */
    private List<BrandVo> brands;
    /**
     * 当前查询到的结果 所有涉及到的属性
     */
    private List<AttrVo> attrs;
    /**
     * 当前查询结果 所设计到的分类
     */
    private List<CatalogVo> catalogs;
    /**
     * 分页
     */
    private List<Integer> pageNavs;

    //=============以上是返回给页面的所有信息====================


    /**
     * 面包屑导航
     */
    private List<NavVo> navs = new ArrayList<>();
    private List<Long> attrIds = new ArrayList<>();

    @Data
    public static class NavVo{
        private String navName;
        private String navValue;
        private String link;
    }
    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }

    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }



}