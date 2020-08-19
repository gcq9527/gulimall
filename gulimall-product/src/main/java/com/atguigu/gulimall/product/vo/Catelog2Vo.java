package com.atguigu.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Guo
 * @Create 2020/7/24 10:17
 */

//二级分类vo
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catelog2Vo {

    private String catalog1Id; //1级分类id
    private List<Catelog3Vo> catalog3List; //三级子分类
    private String id;
    private String name;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catelog3Vo {
        private String catalog2Id; //父分类 二级分类id
        private String id;
        private String name;
    }
}