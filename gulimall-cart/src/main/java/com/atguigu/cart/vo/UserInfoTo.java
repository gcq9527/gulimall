package com.atguigu.cart.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @author Guo
 * @Create 2020/8/3 17:34
 */
@ToString
@Data
public class UserInfoTo {
    private Long userId;
    private String userkey;

    private boolean tempUser = false;
}