package com.atguigu.gulimall.auth.fegin;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.vo.SociaIUser;
import com.atguigu.gulimall.auth.vo.UserLoginVo;
import com.atguigu.gulimall.auth.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Guo
 * @Create 2020/7/31 16:36
 */
@FeignClient("gulimall-member")
public interface MemberFeginService {

    /**
     * 远程调用 gulimall-member服务进行登录验证
     * @param userRegistVo
     * @return
     */
    @PostMapping("/member/member/register")
    public R regist(@RequestBody UserRegistVo userRegistVo);


    /**
     * 远程调用用户登录接口
     * @param userLoginVo
     * @return
     */
    @PostMapping("/member/member/login")
    public R login(@RequestBody UserLoginVo userLoginVo);
    /**
     * 社交登录
     * @param sociaIUserVo
     * @return
     */
    @PostMapping("/member/member/oauth2/login")
    public R oauthlogin(@RequestBody SociaIUser sociaIUserVo) throws Exception;


}