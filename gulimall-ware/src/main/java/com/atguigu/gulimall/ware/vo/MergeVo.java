package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Guo
 * @Create 2020/7/20 10:44
 */
@Data
public class MergeVo {
    private Long purchaseId; //整单id
    private List<Long> items; //【1，2，3，4】 合并并且整合
}