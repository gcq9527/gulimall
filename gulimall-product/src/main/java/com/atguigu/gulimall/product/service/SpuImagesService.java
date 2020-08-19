package com.atguigu.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SpuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * spu图片
 *
 * @author Guo
 * @email sunlightcs@gmail.com
 * @date 2020-07-10 10:17:39
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 批量保存spu图片
     * @param id 商品id
     * @param images
     */
    void saveImages(Long id, List<String> images);

}

