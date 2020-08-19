package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;
import com.atguigu.gulimall.product.vo.AttrResVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author Guo
 * @email sunlightcs@gmail.com
 * @date 2020-07-10 10:17:39
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> param, long catelogId, String type) throws Exception;

    AttrResVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    /**
     * 根据分组id查找关联所有基本属性
     * @param attrgroupId
     * @return
     */
    List<AttrEntity> getRelationAttr(Long attrgroupId);

    /**
     * 删除商品属性
     * @param vos
     */
    void deleteRelation(AttrGroupRelationVo[] vos);

    /**
     * 获取当前分组没有管理的所有属性
     * @param params  分页参数
     * @param attrgroupId 属性分组id
     * @return
     */
    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    /**
     * 在指定的所有属性的集合里面 挑出检索属性
     * @param attrIds
     * @return
     */
    List<Long> saleSearchAttrs(List<Long> attrIds);
}

