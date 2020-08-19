package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Guo
 * @Create 2020/7/20 13:26
 */
@Data
public class PurchaseDoneVo {

    @NotNull
    public Long id; //采购单id

    private List<PurchaseItemDoneVo> items;
}