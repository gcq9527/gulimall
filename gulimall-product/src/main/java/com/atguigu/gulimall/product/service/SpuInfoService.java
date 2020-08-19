package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import com.atguigu.gulimall.product.vo.SpuSaveVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SpuInfoEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author Guo
 * @email sunlightcs@gmail.com
 * @date 2020-07-10 10:17:39
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    /**
     * 保存spu
     * @param spuInfoEntity
     */
    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

    PageUtils queryPageByCondtion(Map<String, Object> params);

    /**
     * 上商品架
     * @param spuId
     */
    void up(Long spuId);

    /**
     * 根据skuid获取到spu信息
     * @param skuId
     * @return
     */
    SpuInfoEntity getSpuInfoBySkuId(Long skuId);

}

