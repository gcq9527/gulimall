package com.atguigu.gulimall.auth.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @author Guo
 * @Create 2020/8/1 13:51
 */
@Data
@ToString
public class SociaIUser {

    private String access_token;
    private String remind_in;
    private long expires_in;
    private String uid;
    private String isRealName;
}