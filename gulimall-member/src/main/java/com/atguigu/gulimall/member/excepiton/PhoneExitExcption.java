package com.atguigu.gulimall.member.excepiton;

/**
 * @author Guo
 * @Create 2020/7/31 14:40
 */
public class PhoneExitExcption extends RuntimeException {
    public PhoneExitExcption() {
        super("手机存在");
    }
}