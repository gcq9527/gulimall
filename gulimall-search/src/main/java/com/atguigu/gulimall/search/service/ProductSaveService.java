package com.atguigu.gulimall.search.service;

import com.atguigu.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author Guo
 * @Create 2020/7/23 14:00
 */
public interface ProductSaveService {
    /**
     * es数据保存
     * @param skuEsModel
     */
    boolean productStatusUp(List<SkuEsModel> skuEsModel) throws IOException;
}