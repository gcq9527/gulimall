package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Guo
 * @Create 2020/7/20 13:27
 */
@Data
public class PurchaseItemDoneVo {

    private Long itemId;
    private Integer status;
    private String reason;
}