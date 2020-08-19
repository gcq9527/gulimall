package com.atguigu.gulimall.member.excepiton;

/**
 * @author Guo
 * @Create 2020/7/31 14:40
 */
public class UsernameExitException extends RuntimeException {
    public UsernameExitException() {
        super("用户名存在");
    }
}