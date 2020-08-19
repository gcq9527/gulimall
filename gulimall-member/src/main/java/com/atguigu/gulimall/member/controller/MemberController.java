package com.atguigu.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.gulimall.member.excepiton.PhoneExitExcption;
import com.atguigu.gulimall.member.excepiton.UsernameExitException;
import com.atguigu.gulimall.member.fegin.CouponFeignService;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.MemberVo;
import com.atguigu.gulimall.member.vo.SociaIUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;


/**
 * 会员
 *
 * @author Guo
 * @email sunlightcs@gmail.com
 * @date 2020-07-10 13:24:37
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    CouponFeignService couponFeignService;


    /**
     * 测试open-fegin远程调用
     *
     * @return
     */
    @RequestMapping("/coupons")
    public R test() {
//        MemberEntity memberEntity = new MemberEntity();
//        memberEntity.setNickname("张三");
        R membercoupns = couponFeignService.membercoupns();
//        membercoupns.get("");

//        return R.ok().put("member",memberEntity).put("coupons",membercoupns.get("coupons"));
        return membercoupns;
    }

    /**
     * 用户登录
     * @param memberLoginVo
     * @return
     */
    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo memberLoginVo){
        MemberEntity entity = memberService.login(memberLoginVo);
        if (entity != null) {
            return R.ok().setData(entity);
        } else {
            return R.error(BizCodeEnume.LOGINACCT_PASSWORD_INVALID_EXCEPTION.getCode(),BizCodeEnume.LOGINACCT_PASSWORD_INVALID_EXCEPTION.getMsg());
        }
    }

    /**
     * 社交登录
     * @param sociaIUserVo
     * @return
     */
    @PostMapping("/oauth2/login")
    public R oauthlogin(@RequestBody SociaIUserVo sociaIUserVo) throws Exception {
        MemberEntity entity = memberService.login(sociaIUserVo);
        if (entity != null) {
            // TODO 1、登录成功处理
            return R.ok().setData(entity);
        } else {
            return R.error(BizCodeEnume.LOGINACCT_PASSWORD_INVALID_EXCEPTION.getCode(),BizCodeEnume.LOGINACCT_PASSWORD_INVALID_EXCEPTION.getMsg());
        }
    }


    /**
     * 用户注册
     *
     * @param memberVo
     */
    @PostMapping("/register")
    public R regist(@RequestBody MemberVo memberVo) {
        try {
            memberService.regist(memberVo);
        } catch (PhoneExitExcption e) {
          return R.error(BizCodeEnume.PHONE_EXICT_EXCEPTION.getCode(),BizCodeEnume.PHONE_EXICT_EXCEPTION.getMsg());
        } catch (UsernameExitException e) {
            return R.error(BizCodeEnume.USERNAME_EXICT_EXCEPTION.getCode(),BizCodeEnume.USERNAME_EXICT_EXCEPTION.getMsg());
        }
        return R.ok();
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
