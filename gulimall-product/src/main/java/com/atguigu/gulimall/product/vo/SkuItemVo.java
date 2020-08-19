package com.atguigu.gulimall.product.vo;

import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author Guo
 * @Create 2020/7/30 8:58
 */
@Data
public class SkuItemVo<list> {

    //1、sku基本信息获取 sku_info
    SkuInfoEntity info;
    /**
     * 是否有货
     */
    boolean hasStock =true;

    //2、sku图片信息 sku_image
    List<SkuImagesEntity> images;

    //3、spu销售属性组合
    List<SkuItemSaleAttrVo> saleAttr;
    //4、获取spu得介绍
    SpuInfoDescEntity desp;

    //5、获取spu得规格参数信息
    List<SpuItemAttrGroupVo> groupAttrs;

    /**
     * 商品秒杀信息vo
     */
    SeckillSkuRedisTo seckillInfo;




}