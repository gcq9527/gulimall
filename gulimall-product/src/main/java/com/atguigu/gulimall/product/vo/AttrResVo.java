package com.atguigu.gulimall.product.vo;

import lombok.Data;
import sun.plugin.dom.core.Attr;

/**
 * 商品属性Vo传输对象
 * @author Guo
 * @Create 2020/7/17 11:08
 */
@Data
public class AttrResVo extends AttrVo {

    /**
     * catelogName 所属于分类名字
     * groupName 主体 所属分类的名字
     */
    private String catelogName;
    private String groupName;

    private Long[] catelogPath;
}