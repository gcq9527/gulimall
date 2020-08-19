package com.atguigu.gulimall.member.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author Guo
 * @Create 2020/7/31 14:17
 */
@Data
public class MemberVo {
    private String userName;
    private String password;
    private String phone;
}