package com.atguigu.gulimall.member.service;

import com.atguigu.gulimall.member.excepiton.PhoneExitExcption;
import com.atguigu.gulimall.member.excepiton.UsernameExitException;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.MemberVo;
import com.atguigu.gulimall.member.vo.SociaIUserVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author Guo
 * @email sunlightcs@gmail.com
 * @date 2020-07-10 13:24:37
 */
public interface MemberService extends IService<MemberEntity> {

    /**
     * 默认查询
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 用户注册
     * @param memberVo
     */
    void regist(MemberVo memberVo);

    /**
     * 检查手机是否唯一
     * @param phone
     */
    void checkPhoneUnique(String phone) throws PhoneExitExcption;

    /**
     * 检查用户名是否唯一
     * @param username
     */
    void checkUserNameUnique(String username) throws UsernameExitException;

    /**
     * 用户登录
     * @param memberLoginVo
     * @return
     */
    MemberEntity login(MemberLoginVo memberLoginVo);

    /**
     * 社交登录
     * @param sociaIUserVo
     * @return
     */
    MemberEntity login(SociaIUserVo sociaIUserVo) throws Exception;

}

