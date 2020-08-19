package com.atguigu.gulimall.member.vo;

import lombok.Data;

/**
 * @author Guo
 * @Create 2020/8/1 21:50
 */
@Data
public class SociaIUserVo {
    private String access_token;
    private String remind_in;
    private long expires_in;
    private String uid;
    private String isRealName;
}